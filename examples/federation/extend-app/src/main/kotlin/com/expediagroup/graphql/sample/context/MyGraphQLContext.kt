package com.expediagroup.graphql.sample.context

import com.expediagroup.graphql.annotations.GraphQLContext
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse

/**
 * Simple [GraphQLContext] that holds extra value.
 */
class MyGraphQLContext(val myCustomValue: String, val request: ServerHttpRequest, val response: ServerHttpResponse)
