package com.expedia.graphql.schema.hooks

import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KType

/**
 * Default hooks that do not override or set anything.
 *
 * To set your own custom hooks, it is easier to extend this class instead of SchemaGeneratorHooks
 * and just override the methods you need.
 */
open class NoopSchemaGeneratorHooks : SchemaGeneratorHooks {

    override fun willBuildSchema(builder: GraphQLSchema.Builder): GraphQLSchema.Builder = builder

    override fun willGenerateGraphQLType(type: KType): GraphQLType? = null

    override fun willResolveMonad(type: KType): KType =
        if (type.classifier == CompletableFuture::class) {
            type.arguments.firstOrNull()?.type ?: type
        } else {
            type
        }

    override fun isValidProperty(property: KProperty<*>) = true

    override fun isValidFunction(function: KFunction<*>) = true

    override fun didGenerateGraphQLType(type: KType, generatedType: GraphQLType) = Unit

    override fun didGenerateDataFetcher(function: KFunction<*>, dataFetcher: DataFetcher<*>) = dataFetcher

    override fun didGenerateQueryType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition) = fieldDefinition

    override fun didGenerateMutationType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition) = fieldDefinition
}
