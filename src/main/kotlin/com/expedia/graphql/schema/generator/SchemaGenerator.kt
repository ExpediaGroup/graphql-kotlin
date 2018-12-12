package com.expedia.graphql.schema.generator

import com.expedia.graphql.TopLevelObjectDef
import com.expedia.graphql.schema.SchemaGeneratorConfig
import com.expedia.graphql.schema.extensions.getValidFunctions
import com.expedia.graphql.schema.generator.state.SchemaGeneratorState
import com.expedia.graphql.schema.generator.types.EnumTypeBuilder
import com.expedia.graphql.schema.generator.types.FunctionTypeBuilder
import com.expedia.graphql.schema.generator.types.InputObjectTypeBuilder
import com.expedia.graphql.schema.generator.types.InterfaceTypeBuilder
import com.expedia.graphql.schema.generator.types.ListTypeBuilder
import com.expedia.graphql.schema.generator.types.ObjectTypeBuilder
import com.expedia.graphql.schema.generator.types.PropertyTypeBuilder
import com.expedia.graphql.schema.generator.types.ScalarTypeBuilder
import com.expedia.graphql.schema.generator.types.UnionTypeBuilder
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KType

@Suppress("Detekt.TooManyFunctions")
internal class SchemaGenerator(
    private val queries: List<TopLevelObjectDef>,
    private val mutations: List<TopLevelObjectDef>,
    internal val config: SchemaGeneratorConfig
) {

    internal val state = SchemaGeneratorState(config.supportedPackages)
    internal val subTypeMapper = SubTypeMapper(config.supportedPackages)

    private val objectTypeBuilder = ObjectTypeBuilder(this)
    private val unionTypeBuilder = UnionTypeBuilder(this)
    private val interfaceTypeBuilder = InterfaceTypeBuilder(this)
    private val propertyTypeBuilder = PropertyTypeBuilder(this)
    private val inputObjectTypeBuilder = InputObjectTypeBuilder(this)
    private val listTypeBuilder = ListTypeBuilder(this)
    private val functionTypeBuilder = FunctionTypeBuilder(this)
    private val enumTypeBuilder = EnumTypeBuilder(this)
    private val scalarTypeBuilder = ScalarTypeBuilder(this)

    internal fun generate(): GraphQLSchema {
        val builder = GraphQLSchema.newSchema()

        addQueries(builder)
        addMutations(builder)
        addAdditionalTypes(builder)
        addDirectives(builder)

        return config.hooks.willBuildSchema(builder).build()
    }

    private fun addAdditionalTypes(builder: GraphQLSchema.Builder) =
        state.getValidAdditionalTypes().forEach { builder.additionalType(it) }

    private fun addDirectives(builder: GraphQLSchema.Builder) =
        builder.additionalDirectives(state.directives)

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

    internal fun function(fn: KFunction<*>, target: Any? = null, abstract: Boolean = false) =
        functionTypeBuilder.function(fn, target, abstract)

    internal fun property(prop: KProperty<*>) =
        propertyTypeBuilder.property(prop)

    internal fun listType(type: KType, inputType: Boolean) =
        listTypeBuilder.listType(type, inputType)

    internal fun arrayType(type: KType, inputType: Boolean) =
            listTypeBuilder.arrayType(type, inputType)

    internal fun objectType(kClass: KClass<*>, interfaceType: GraphQLInterfaceType? = null) =
        objectTypeBuilder.objectType(kClass, interfaceType)

    internal fun inputObjectType(kClass: KClass<*>) =
        inputObjectTypeBuilder.inputObjectType(kClass)

    internal fun interfaceType(kClass: KClass<*>) =
        interfaceTypeBuilder.interfaceType(kClass)

    internal fun unionType(kClass: KClass<*>) =
        unionTypeBuilder.unionType(kClass)

    internal fun enumType(kClass: KClass<out Enum<*>>) =
        enumTypeBuilder.enumType(kClass)

    internal fun scalarType(type: KType, annotatedAsID: Boolean = false) =
        scalarTypeBuilder.scalarType(type, annotatedAsID)
}
