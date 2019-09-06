package com.expediagroup.graphql

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.generator.SchemaGenerator
import graphql.schema.GraphQLSchema

/**
 * Entry point to generate a graphql schema using reflection on the passed objects.
 *
 * @param config schema generation configuration
 * @param queries list of [TopLevelObject] to use for GraphQL queries
 * @param mutations optional list of [TopLevelObject] to use for GraphQL mutations
 * @param subscriptions optional list of [TopLevelObject] to use for GraphQL subscriptions
 *
 * @return GraphQLSchema from graphql-java
 */
@Throws(GraphQLKotlinException::class)
fun toSchema(
    config: SchemaGeneratorConfig,
    queries: List<TopLevelObject>,
    mutations: List<TopLevelObject> = emptyList(),
    subscriptions: List<TopLevelObject> = emptyList()
): GraphQLSchema {
    val generator = SchemaGenerator(config)
    return generator.generate(queries, mutations, subscriptions)
}
