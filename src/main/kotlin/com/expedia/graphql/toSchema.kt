package com.expedia.graphql

import com.expedia.graphql.exceptions.GraphQLKotlinException
import com.expedia.graphql.generator.SchemaGenerator
import graphql.schema.GraphQLSchema

/**
 * Entry point to generate a graphql schema using reflection on the passed objects.
 *
 * @param config Schema generation configuration
 * @param queries List of [TopLevelObject] to use for GraphQL queries
 * @param mutations List of [TopLevelObject] to use for GraphQL mutations
 *
 * @return GraphQLSchema from graphql-java
 */
@Throws(GraphQLKotlinException::class)
fun toSchema(
    config: SchemaGeneratorConfig,
    queries: List<TopLevelObject>,
    mutations: List<TopLevelObject> = emptyList()
): GraphQLSchema {
    val generator = SchemaGenerator(config)
    return generator.generate(queries, mutations)
}
