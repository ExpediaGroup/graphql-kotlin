package com.expedia.graphql.annotations

/**
 * Marks something as experimental feature.
 */
@GraphQLDirective(
        name = "Experimental",
        description = "Directs the GraphQL schema that target feature is marked as experimental and can be changed without notice"
)
annotation class GraphQLExperimental(val value: String)