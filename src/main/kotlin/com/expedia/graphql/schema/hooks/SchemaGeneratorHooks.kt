package com.expedia.graphql.schema.hooks

import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KType

interface SchemaGeneratorHooks {
    /**
     * Called before the final GraphQL schema is built.
     * This doesn't prevent the called from rebuilding the final schema using java-graphql's functionality
     */
    fun willBuildSchema(builder: GraphQLSchema.Builder): GraphQLSchema.Builder

    /**
     * Called before using reflection to generate the graphql object type for the given KType.
     * This allows supporting objects that the caller does not want to use reflection on for special handling
     */
    fun willGenerateGraphQLType(type: KType): GraphQLType?

    /**
     * Called when looking at the KClass properties to determine if it valid for adding to the generated schema.
     * If any filter returns false, it is rejected.
     */
    fun isValidProperty(property: KProperty<*>): Boolean

    /**
     * Called when looking at the KClass functions to determine if it valid for adding to the generated schema.
     * If any filter returns false, it is rejected.
     */
    fun isValidFunction(function: KFunction<*>): Boolean

    /**
     * Called after wrapping the type based on nullity but before adding the generated type to the schema
     */
    fun didGenerateGraphQLType(type: KType, generatedType: GraphQLType)

    /**
     * Called after converting the function to a data fetcher allowing wrapping the fetcher to modify data or instrument it.
     * This is more useful than the graphql.execution.instrumentation.Instrumentation as you have the function type here
     */
    fun didGenerateDataFetcher(function: KFunction<*>, dataFetcher: DataFetcher<*>): DataFetcher<*>

    /**
     * Called after converting the function to a field definition but before adding to the schema to allow customization
     */
    fun didGenerateQueryType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition

    /**
     * Called after converting the function to a field definition but before adding to the schema to allow customization
     */
    fun didGenerateMutationType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition
}
