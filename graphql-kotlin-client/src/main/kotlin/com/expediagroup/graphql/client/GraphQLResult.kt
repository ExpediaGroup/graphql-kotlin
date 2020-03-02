package com.expediagroup.graphql.client

data class GraphQLResult<T>(
    val data: T? = null,
    val errors: List<GraphQLError>? = null,
    val extensions: Map<Any, Any>? = null
)
