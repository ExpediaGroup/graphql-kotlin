package com.expedia.graphql.directives

import graphql.schema.DataFetcher
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition

/**
 * KotlinSchemaDirectiveEnvironment holds basic wiring information that includes target element and directive that is to be applied.
 */
open class KotlinSchemaDirectiveEnvironment<out T : GraphQLDirectiveContainer>(
    val element: T,
    val directive: GraphQLDirective
)

/**
 * KotlinFieldDirectiveEnvironment holds wiring information for applying directives on GraphQL fields.
 */
class KotlinFieldDirectiveEnvironment(
    field: GraphQLFieldDefinition,
    fieldDirective: GraphQLDirective,
    private val coordinates: FieldCoordinates,
    private val codeRegistry: GraphQLCodeRegistry.Builder
) : KotlinSchemaDirectiveEnvironment<GraphQLFieldDefinition>(element = field, directive = fieldDirective) {

    fun getDataFetcher(): DataFetcher<Any> = codeRegistry.getDataFetcher(coordinates, element)

    fun setDataFetcher(newDataFetcher: DataFetcher<Any>) {
        codeRegistry.dataFetcher(coordinates, newDataFetcher)
    }
}
