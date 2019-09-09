package com.expediagroup.graphql.directives

import graphql.schema.DataFetcher
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition

/**
 * KotlinFieldDirectiveEnvironment holds wiring information for applying directives on GraphQL fields.
 */
class KotlinFieldDirectiveEnvironment(
    field: GraphQLFieldDefinition,
    fieldDirective: GraphQLDirective,
    private val coordinates: FieldCoordinates,
    private val codeRegistry: GraphQLCodeRegistry.Builder
) : KotlinSchemaDirectiveEnvironment<GraphQLFieldDefinition>(element = field, directive = fieldDirective) {

    /**
     * Retrieve current data fetcher associated with the target element.
     */
    fun getDataFetcher(): DataFetcher<Any> = codeRegistry.getDataFetcher(coordinates, element)

    /**
     * Update target element data fetcher.
     */
    fun setDataFetcher(newDataFetcher: DataFetcher<Any>) {
        codeRegistry.dataFetcher(coordinates, newDataFetcher)
    }
}
