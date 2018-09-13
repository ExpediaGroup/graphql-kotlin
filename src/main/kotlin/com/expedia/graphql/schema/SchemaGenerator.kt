package com.expedia.graphql.schema

import com.expedia.graphql.KotlinDataFetcher
import com.expedia.graphql.Parameter
import com.expedia.graphql.TopLevelObjectDef
import com.expedia.graphql.schema.exceptions.ConflictingTypesException
import com.expedia.graphql.schema.exceptions.TypeNotSupportedException
import com.expedia.graphql.schema.hooks.SchemaGeneratorHooks
import com.expedia.graphql.schema.models.KGraphQLType
import graphql.TypeResolutionEnvironment
import graphql.schema.DataFetcher
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLEnumType
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
import org.reflections.Reflections
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

internal class SchemaGenerator(
    private val queries: List<TopLevelObjectDef>,
    private val mutations: List<TopLevelObjectDef>,
    private val config: SchemaConfig,
    private val hooks: SchemaGeneratorHooks
) {

    private val typesCache: MutableMap<String, KGraphQLType> = mutableMapOf()
    private val propertyFilters: List<PropertyFilter> = listOf(isPublic, isNotGraphQLIgnored)
    private val functionFilters: List<FunctionFilter> = listOf(isPublic, isNotGraphQLIgnored, isNotBlackListed)
    private val additionTypes = mutableSetOf<GraphQLType>()

    internal fun generate(): GraphQLSchema {
        val builder = generateWithReflection()
        return hooks.willBuildSchema(builder).build()
    }

    private fun generateWithReflection(): GraphQLSchema.Builder {
        val builder = GraphQLSchema.newSchema()
        addQueries(builder)
        addMutations(builder)
        addAdditionalTypes(builder)
        return builder
    }

    private fun addAdditionalTypes(builder: GraphQLSchema.Builder) {
        builder.additionalTypes(additionTypes)
    }

    private fun addQueries(builder: GraphQLSchema.Builder) {
        val queryBuilder = GraphQLObjectType.Builder()
        queryBuilder.name(config.topLevelQueryName)
        for (query in queries) {
            query.klazz.declaredMemberFunctions
                .filter { hooks.isValidFunction(it) }
                .filter { func -> functionFilters.all { it.invoke(func) } }
                .forEach {
                    val function = function(it, query.obj)
                    val functionFromHook = hooks.didGenerateQueryType(it, function)
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
                mutation.klazz.declaredMemberFunctions
                    .filter { hooks.isValidFunction(it) }
                    .filter { func -> functionFilters.all { it.invoke(func) } }
                    .forEach {
                        val function = function(it, mutation.obj)
                        val functionFromHook = hooks.didGenerateMutationType(it, function)
                        mutationBuilder.field(functionFromHook)
                    }
            }

            builder.mutation(mutationBuilder.build())
        }
    }

    private fun function(fn: KFunction<*>, target: Any? = null): GraphQLFieldDefinition {
        val builder = GraphQLFieldDefinition.newFieldDefinition()
        builder.name(fn.name)
        builder.description(fn.graphQLDescription())
        fn.getDeprecationReason()?.let {
            builder.deprecate(it)
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
                args[name!!] = Parameter(it.type.jvmErasure.java, it.annotations)
            }
        }
        val dataFetcher: DataFetcher<*> = KotlinDataFetcher(target, fn, args, fn.isGraphQLInstrumentable())
        val hookDataFetcher = hooks.didGenerateDataFetcher(fn, dataFetcher)
        builder.dataFetcher(hookDataFetcher)
        builder.type(graphQLTypeOf(fn.returnType) as GraphQLOutputType)
        return builder.build()
    }

    private fun property(prop: KProperty<*>): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .description(prop.graphQLDescription())
        .name(prop.name)
        .type(graphQLTypeOf(prop.returnType) as GraphQLOutputType)
        .deprecate(prop.getDeprecationReason())
        .build()

    private fun argument(parameter: KParameter): GraphQLArgument = GraphQLArgument.newArgument()
        .name(parameter.name)
        .description(parameter.graphQLDescription() ?: parameter.type.graphQLDescription())
        .type(graphQLTypeOf(parameter.type, true) as GraphQLInputType)
        .build()

    private fun graphQLTypeOf(type: KType, inputType: Boolean = false): GraphQLType {
        val hookGraphQLType = hooks.willGenerateGraphQLType(type)
        val graphQLType = hookGraphQLType ?: graphQLScalar(type) ?: objectFromReflection(type, inputType)
        val typeWithNullityTakenIntoAccount = graphQLType.wrapInNonNull(type)
        hooks.didGenerateGraphQLType(type, typeWithNullityTakenIntoAccount)
        return typeWithNullityTakenIntoAccount
    }

    private fun objectFromReflection(type: KType, inputType: Boolean): GraphQLType {
        val kClass = type.classifier as KClass<*>
        val cacheKey = getCacheKey(kClass, type, inputType)
        val cachedType = typesCache[cacheKey]

        if (cachedType != null) {
            val isSameNameButNotSameClass = cachedType.kClass != kClass
            when {
                isSameNameButNotSameClass -> throw ConflictingTypesException(cachedType.kClass, kClass)
                else -> return cachedType.graphQLType
            }
        }

        val graphQLType = getGraphQLType(kClass, inputType, type)

        typesCache[cacheKey] = KGraphQLType(kClass, graphQLType)

        return graphQLType
    }

    private fun getGraphQLType(kClass: KClass<*>, inputType: Boolean, type: KType): GraphQLType = when {
        kClass.isSubclassOf(Enum::class) -> enumType(kClass)
        kClass.isSubclassOf(List::class) || kClass.java.isArray -> listType(type, inputType)
        kClass.java.isInterface -> interfaceType(kClass)
        else -> if (inputType) inputObjectType(kClass) else objectType(kClass)
    }

    private fun getCacheKey(kClass: KClass<*>, type: KType, inputType: Boolean): String {
        if (kClass.isSubclassOf(Enum::class)) {
            return kClass.simpleName ?: throw CouldNotGetNameOfEnumException()
        }
        val cacheKeyFromTypeName = when {
            kClass.isSubclassOf(List::class) -> "List<${type.arguments.first().type!!.jvmErasure.simpleName}>"
            kClass.java.isArray -> "Array<${type.arguments.first().type!!.jvmErasure.simpleName}>"
            else -> {
                throwIfTypeIsNotSupported(type)
                type.jvmErasure.simpleName
            }
        }

        return "$cacheKeyFromTypeName:$inputType"
    }

    @Throws(TypeNotSupportedException::class)
    private fun throwIfTypeIsNotSupported(type: KType) {
        val qualifiedName = type.jvmErasure.qualifiedName ?: ""
        val comesFromSupportedPackageName = qualifiedName.startsWith(config.supportedPackages)
        if (!comesFromSupportedPackageName) {
            throw TypeNotSupportedException(qualifiedName, config.supportedPackages)
        }
    }

    private fun enumType(kClass: KClass<*>): GraphQLEnumType {
        val enumKClass = @Suppress("UNCHECKED_CAST") (kClass as KClass<Enum<*>>)
        val builder = GraphQLEnumType.newEnum()
        enumKClass.java.enumConstants.forEach {
            builder.value(it.name)
        }
        builder.name(enumKClass.simpleName)
        return builder.build()
    }

    private fun listType(type: KType, inputType: Boolean): GraphQLList =
        GraphQLList.list(graphQLTypeOf(type.arguments.first().type!!, inputType))

    private fun objectType(klass: KClass<*>, interfaceType: GraphQLInterfaceType? = null): GraphQLType {
        val builder = GraphQLObjectType.newObject()

        builder.name(klass.simpleName)
        builder.description(klass.graphQLDescription())

        klass.directives().map {
            builder.withDirective(it)
        }

        if (interfaceType != null) builder.withInterface(interfaceType)

        klass.declaredMemberProperties
            .filter { hooks.isValidProperty(it) }
            .filter { prop -> propertyFilters.all { it.invoke(prop) } }
            .forEach { builder.field(property(it)) }

        klass.declaredMemberFunctions
            .filter { hooks.isValidFunction(it) }
            .filter { func -> functionFilters.all { it.invoke(func) } }
            .forEach { builder.field(function(it)) }

        return builder.build()
    }

    private fun inputObjectType(klass: KClass<*>): GraphQLType {
        val builder = GraphQLInputObjectType.newInputObject()
        val name = getGraphQLClassName(klass, true)

        builder.name(name)
        builder.description(klass.graphQLDescription())

        // It does not make sense to run functions against the input types so we only process data fields
        klass.declaredMemberProperties
            .filter { hooks.isValidProperty(it) }
            .filter { prop -> propertyFilters.all { it.invoke(prop) } }
            .forEach { builder.field(inputProperty(it)) }

        return builder.build()
    }

    private fun inputProperty(prop: KProperty<*>): GraphQLInputObjectField {
        val builder = GraphQLInputObjectField.newInputObjectField()

        builder.description(prop.graphQLDescription())
        builder.name(prop.name)
        builder.type(graphQLTypeOf(prop.returnType, true) as GraphQLInputType)

        return builder.build()
    }

    private fun interfaceType(kClass: KClass<*>): GraphQLType {
        val builder = GraphQLInterfaceType.newInterface()

        builder.name(kClass.simpleName)
        builder.description(kClass.graphQLDescription())

        kClass.declaredMemberProperties
            .filter { hooks.isValidProperty(it) }
            .filter { prop -> propertyFilters.all { it.invoke(prop) } }
            .forEach { builder.field(property(it)) }

        val reflections = Reflections(config.supportedPackages)
        val implementations = reflections.getSubTypesOf(Class.forName(kClass.javaObjectType.name))

        implementations.forEach {
            builder.typeResolver { env: TypeResolutionEnvironment? -> env?.schema?.getObjectType(it?.simpleName) }
        }

        val interfaceType = builder.build()

        implementations.forEach {
            additionTypes.add(objectType(it.kotlin, interfaceType))
        }

        return interfaceType
    }
}
