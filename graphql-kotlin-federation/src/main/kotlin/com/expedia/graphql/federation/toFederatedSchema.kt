package com.expedia.graphql.federation

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.exceptions.GraphQLKotlinException
import graphql.schema.GraphQLSchema

/**
 * Entry point to generate federated graphql schema using reflection on the passed objects.
 *
 * @param config federated schema generation configuration
 * @param queries optional list of [TopLevelObject] to use for GraphQL queries
 * @param mutations optional list of [TopLevelObject] to use for GraphQL mutations
 * @param subscriptions optional list of [TopLevelObject] to use for GraphQL subscriptions
 *
 * @return GraphQLSchema from graphql-java
 */
@Throws(GraphQLKotlinException::class)
fun toFederatedSchema(
    config: FederatedSchemaGeneratorConfig,
    queries: List<TopLevelObject> = emptyList(),
    mutations: List<TopLevelObject> = emptyList(),
    subscriptions: List<TopLevelObject> = emptyList()
): GraphQLSchema {
    val generator = FederatedSchemaGenerator(config)
    return generator.generate(queries, mutations, subscriptions)
}
