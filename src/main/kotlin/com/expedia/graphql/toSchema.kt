package com.expedia.graphql

import com.expedia.graphql.exceptions.InvalidSchemaException
import com.expedia.graphql.generator.SchemaGenerator
import graphql.GraphQLException
import graphql.schema.GraphQLSchema

/**
 * Entry point to generate a graphql schema using reflection on the passed objects.
 *
 * @param queries List of [TopLevelObject] objects to use for GraphQL queries
 * @param mutations List of [TopLevelObject] objects to use for GraphQL mutations
 * @param config Schema generation configuration
 */
@Throws(GraphQLException::class)
fun toSchema(
    queries: List<TopLevelObject>,
    mutations: List<TopLevelObject> = emptyList(),
    config: SchemaGeneratorConfig
): GraphQLSchema {
    if (queries.isEmpty()) {
        throw InvalidSchemaException()
    }
    return SchemaGenerator(queries, mutations, config).generate()
}
