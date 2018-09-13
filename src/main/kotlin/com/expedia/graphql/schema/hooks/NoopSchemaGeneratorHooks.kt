package com.expedia.graphql.schema.hooks

import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KType

open class NoopSchemaGeneratorHooks : SchemaGeneratorHooks {

    override fun willBuildSchema(builder: GraphQLSchema.Builder): GraphQLSchema.Builder = builder

    override fun willGenerateGraphQLType(type: KType): GraphQLType? = null

    override fun isValidProperty(property: KProperty<*>) = true

    override fun isValidFunction(function: KFunction<*>) = true

    override fun didGenerateGraphQLType(type: KType, generatedType: GraphQLType) = Unit

    override fun didGenerateDataFetcher(function: KFunction<*>, dataFetcher: DataFetcher<*>) = dataFetcher

    override fun didGenerateQueryType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition) = fieldDefinition

    override fun didGenerateMutationType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition) = fieldDefinition
}
