package com.expedia.graphql.schema.generator

import com.expedia.graphql.TopLevelObjectDef
import com.expedia.graphql.schema.KotlinDataFetcher
import com.expedia.graphql.schema.Parameter
import com.expedia.graphql.schema.SchemaGeneratorConfig
import com.expedia.graphql.schema.extensions.canBeGraphQLInterface
import com.expedia.graphql.schema.extensions.canBeGraphQLUnion
import com.expedia.graphql.schema.extensions.directives
import com.expedia.graphql.schema.extensions.getDeprecationReason
import com.expedia.graphql.schema.extensions.getTypeOfFirstArgument
import com.expedia.graphql.schema.extensions.getValidFunctions
import com.expedia.graphql.schema.extensions.getValidProperties
import com.expedia.graphql.schema.extensions.graphQLDescription
import com.expedia.graphql.schema.extensions.isGraphQLContext
import com.expedia.graphql.schema.extensions.isGraphQLID
import com.expedia.graphql.schema.extensions.throwIfUnathorizedInterface
import com.expedia.graphql.schema.extensions.wrapInNonNull
import com.expedia.graphql.schema.generator.state.SchemaGeneratorState
import com.expedia.graphql.schema.generator.types.defaultGraphQLScalars
import com.expedia.graphql.schema.generator.types.enumType
import com.expedia.graphql.schema.generator.types.getInputClassName
import com.expedia.graphql.schema.models.KGraphQLType
import graphql.TypeResolutionEnvironment
import graphql.schema.DataFetcher
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLUnionType
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaType

@Suppress("Detekt.UnsafeCast")
internal class SchemaGenerator(
    private val queries: List<TopLevelObjectDef>,
    private val mutations: List<TopLevelObjectDef>,
    private val config: SchemaGeneratorConfig
) {

    private val state = SchemaGeneratorState(config.supportedPackages)
    private val subTypeMapper = SubTypeMapper(config.supportedPackages)

    internal fun generate(): GraphQLSchema {
        val builder = generateWithReflection()
        return config.hooks.willBuildSchema(builder).build()
    }

    private fun generateWithReflection(): GraphQLSchema.Builder {
        val builder = GraphQLSchema.newSchema()
        addQueries(builder)
        addMutations(builder)
        addAdditionalTypes(builder)
        addDirectives(builder)
        return builder
    }

    private fun addAdditionalTypes(builder: GraphQLSchema.Builder) {
        state.getValidAdditionalTypes().forEach { builder.additionalType(it) }
    }

    private fun addDirectives(builder: GraphQLSchema.Builder) = builder.additionalDirectives(state.directives)

    private fun addQueries(builder: GraphQLSchema.Builder) {
        val queryBuilder = GraphQLObjectType.Builder()
        queryBuilder.name(config.topLevelQueryName)

        for (query in queries) {
            query.klazz.getValidFunctions(config.hooks)
                .forEach {
                    val function = function(it, query.obj)
                    val functionFromHook = config.hooks.didGenerateQueryType(it, function)
                    queryBuilder.field(functionFromHook)
                }
        }

        builder.query(queryBuilder.build())
    }

    private fun addMutations(builder: GraphQLSchema.Builder) {
        if (mutations.isNotEmpty()) {
            val mutationBuilder = GraphQLObjectType.Builder()
            mutationBuilder.name(config.topLevelMutationName)

            for (mutation in mutations) {
                mutation.klazz.getValidFunctions(config.hooks)
                    .forEach {
                        val function = function(it, mutation.obj)
                        val functionFromHook = config.hooks.didGenerateMutationType(it, function)
                        mutationBuilder.field(functionFromHook)
                    }
            }

            builder.mutation(mutationBuilder.build())
        }
    }

    private fun function(fn: KFunction<*>, target: Any? = null, abstract: Boolean = false): GraphQLFieldDefinition {
        val builder = GraphQLFieldDefinition.newFieldDefinition()
        builder.name(fn.name)
        builder.description(fn.graphQLDescription())

        fn.getDeprecationReason()?.let {
            builder.deprecate(it)
        }

        fn.directives().forEach {
            builder.withDirective(it)
            state.directives.add(it)
        }

        val args = mutableMapOf<String, Parameter>()
        fn.valueParameters.forEach {
            if (!it.isGraphQLContext()) {
                // deprecation of arguments is currently unsupported: https://github.com/facebook/graphql/issues/197
                builder.argument(argument(it))
            }

            val name = it.name
            if (name.isNullOrBlank()) {
                throw IllegalArgumentException("argument name is null or blank, $it")
            } else {
                // Kotlin 1.3 will support contracts, until then we need to force non-null
                @Suppress("Detekt.UnsafeCallOnNullableType")
                args[name!!] = Parameter(it.type.javaType as Class<*>, it.annotations)
            }
        }

        if (!abstract) {
            val dataFetcher: DataFetcher<*> = KotlinDataFetcher(target, fn, args, config.hooks.dataFetcherExecutionPredicate)
            val hookDataFetcher = config.hooks.didGenerateDataFetcher(fn, dataFetcher)
            builder.dataFetcher(hookDataFetcher)
        }

        val monadType = config.hooks.willResolveMonad(fn.returnType)
        builder.type(graphQLTypeOf(monadType) as GraphQLOutputType)
        return builder.build()
    }

    private fun property(prop: KProperty<*>): GraphQLFieldDefinition {
        val propertyType = graphQLTypeOf(type = prop.returnType, annotatedAsID = prop.isGraphQLID()) as GraphQLOutputType

        val fieldBuilder = GraphQLFieldDefinition.newFieldDefinition()
                .description(prop.graphQLDescription())
                .name(prop.name)
                .type(propertyType)
                .deprecate(prop.getDeprecationReason())

        return if (config.dataFetcherFactory != null && prop.isLateinit) {
            updatePropertyFieldBuilder(propertyType, fieldBuilder, config.dataFetcherFactory)
        } else {
            fieldBuilder
        }.build()
    }

    private fun argument(parameter: KParameter): GraphQLArgument {
        parameter.throwIfUnathorizedInterface()
        return GraphQLArgument.newArgument()
            .name(parameter.name)
            .description(parameter.graphQLDescription() ?: parameter.type.graphQLDescription())
            .type(graphQLTypeOf(parameter.type, true) as GraphQLInputType)
            .build()
    }

    private fun graphQLTypeOf(type: KType, inputType: Boolean = false, annotatedAsID: Boolean = false): GraphQLType {
        val hookGraphQLType = config.hooks.willGenerateGraphQLType(type)
        val graphQLType = hookGraphQLType ?: defaultGraphQLScalars(type, annotatedAsID) ?: objectFromReflection(type, inputType)
        val typeWithNullityTakenIntoAccount = graphQLType.wrapInNonNull(type)
        config.hooks.didGenerateGraphQLType(type, typeWithNullityTakenIntoAccount)
        return typeWithNullityTakenIntoAccount
    }

    private fun objectFromReflection(type: KType, inputType: Boolean): GraphQLType {
        val cacheKey = TypesCacheKey(type, inputType)
        val cachedType = state.cache.get(cacheKey)

        if (cachedType != null) {
            return cachedType
        }

        val kClass = type.classifier as KClass<*>
        val graphQLType = getGraphQLType(kClass, inputType, type)
        val kGraphQLType = KGraphQLType(kClass, graphQLType)

        state.cache.put(cacheKey, kGraphQLType)

        return graphQLType
    }

    private fun getGraphQLType(kClass: KClass<*>, inputType: Boolean, type: KType): GraphQLType = when {
        kClass.isSubclassOf(Enum::class) -> @Suppress("UNCHECKED_CAST") enumType(kClass as KClass<Enum<*>>)
        kClass.isSubclassOf(List::class) || kClass.java.isArray -> listType(type, inputType)
        kClass.canBeGraphQLUnion() -> unionType(kClass)
        kClass.canBeGraphQLInterface() -> interfaceType(kClass)
        else -> if (inputType) inputObjectType(kClass) else objectType(kClass)
    }

    private fun listType(type: KType, inputType: Boolean): GraphQLList =
        GraphQLList.list(graphQLTypeOf(type.getTypeOfFirstArgument(), inputType))

    private fun objectType(kClass: KClass<*>, interfaceType: GraphQLInterfaceType? = null): GraphQLType {
        return state.cache.buildIfNotUnderConstruction(kClass) { _ ->
            val builder = GraphQLObjectType.newObject()

            builder.name(kClass.simpleName)
            builder.description(kClass.graphQLDescription())

            kClass.directives().forEach {
                builder.withDirective(it)
                state.directives.add(it)
            }

            if (interfaceType != null) {
                builder.withInterface(interfaceType)
            } else {
                kClass.superclasses
                        .asSequence()
                        .filter { it.canBeGraphQLInterface() && !it.canBeGraphQLUnion() }
                        .map { objectFromReflection(it.createType(), false) as GraphQLInterfaceType }
                        .forEach { builder.withInterface(it) }
            }

            kClass.getValidProperties(config.hooks)
                    .forEach { builder.field(property(it)) }

            kClass.getValidFunctions(config.hooks)
                    .forEach { builder.field(function(it)) }

            builder.build()
        }
    }

    private fun inputObjectType(kClass: KClass<*>): GraphQLType {
        val builder = GraphQLInputObjectType.newInputObject()
        val name = getInputClassName(kClass)

        builder.name(name)
        builder.description(kClass.graphQLDescription())

        // It does not make sense to run functions against the input types so we only process data fields
        kClass.getValidProperties(config.hooks)
            .forEach { builder.field(inputProperty(it)) }

        return builder.build()
    }

    private fun inputProperty(prop: KProperty<*>): GraphQLInputObjectField {
        val builder = GraphQLInputObjectField.newInputObjectField()

        builder.description(prop.graphQLDescription())
        builder.name(prop.name)
        builder.type(graphQLTypeOf(prop.returnType, true, prop.isGraphQLID()) as GraphQLInputType)

        return builder.build()
    }

    private fun interfaceType(kClass: KClass<*>): GraphQLType {
        return state.cache.buildIfNotUnderConstruction(kClass) {
            val builder = GraphQLInterfaceType.newInterface()

            builder.name(kClass.simpleName)
            builder.description(kClass.graphQLDescription())

            kClass.getValidProperties(config.hooks)
                    .forEach { builder.field(property(it)) }

            kClass.getValidFunctions(config.hooks)
                    .forEach { builder.field(function(it, abstract = true)) }

            builder.typeResolver { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObject<Any>().javaClass.simpleName) }
            val interfaceType = builder.build()

            val implementations = subTypeMapper.getSubTypesOf(kClass)
            implementations
                    .filterNot { it.kotlin.isAbstract }
                    .forEach {
                        val objectType = objectType(it.kotlin, interfaceType)
                        val key = TypesCacheKey(it.kotlin.createType(), false)

                        state.cache.put(key, KGraphQLType(it.kotlin, objectType))
                        if (objectType !is GraphQLTypeReference) {
                            state.additionalTypes.add(objectType)
                        }
                        state.cache.removeTypeUnderConstruction(it.kotlin)
                    }

            interfaceType
        }
    }

    private fun unionType(kClass: KClass<*>): GraphQLType {
        return state.cache.buildIfNotUnderConstruction(kClass) {
            val builder = GraphQLUnionType.newUnionType()

            builder.name(kClass.simpleName)
            builder.description(kClass.graphQLDescription())
            builder.typeResolver { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObject<Any>().javaClass.simpleName) }

            val implementations = subTypeMapper.getSubTypesOf(kClass)
            implementations
                    .filterNot { it.kotlin.isAbstract }
                    .forEach {
                        val objectType = state.cache.get(TypesCacheKey(it.kotlin.createType(), false)) ?: objectType(it.kotlin)

                        val key = TypesCacheKey(it.kotlin.createType(), false)

                        if (objectType is GraphQLTypeReference) {
                            builder.possibleType(objectType)
                        } else {
                            builder.possibleType(objectType as GraphQLObjectType)
                        }

                        state.cache.put(key, KGraphQLType(it.kotlin, objectType))
                        if (state.cache.doesNotContain(it.kotlin)) {
                            state.cache.put(key, KGraphQLType(it.kotlin, objectType))
                        }
                    }

            builder.build()
        }
    }
}
