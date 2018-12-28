package com.expedia.graphql

import com.expedia.graphql.exceptions.GraphQLKotlinException
import com.expedia.graphql.generator.SchemaGenerator
import graphql.schema.GraphQLSchema

/**
 * Entry point to generate a graphql schema using reflection on the passed objects.
 *
 * @param queries List of [TopLevelObject] to use for GraphQL queries
 * @param mutations List of [TopLevelObject] to use for GraphQL mutations
 * @param config Schema generation configuration
 *
 * @return GraphQLSchema from graphql-java
 */
@Throws(GraphQLKotlinException::class)
fun toSchema(
    queries: List<TopLevelObject>,
    mutations: List<TopLevelObject> = emptyList(),
    config: SchemaGeneratorConfig
): GraphQLSchema {
    val generator = SchemaGenerator(config)
    return generator.generate(queries, mutations)
}
