package com.expedia.graphql.annotations

/**
 * Set the GraphQL DataFetcher Prefix to be picked up by the schema generator.
 */
@Target(AnnotationTarget.CLASS)
annotation class GraphQLDataFetcherPrefix(val value: String)
