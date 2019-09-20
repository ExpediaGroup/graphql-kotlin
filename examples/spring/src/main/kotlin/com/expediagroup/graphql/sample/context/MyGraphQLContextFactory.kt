package com.expediagroup.graphql.sample.context

import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component

/**
 * [GraphQLContextFactory] that generates [MyGraphQLContext] that will be available when processing GraphQL requests.
 */
@Component
class MyGraphQLContextFactory: GraphQLContextFactory<MyGraphQLContext> {

    override suspend fun generateContext(request: ServerHttpRequest, response: ServerHttpResponse): MyGraphQLContext = MyGraphQLContext(
            myCustomValue = request.headers.getFirst("MyHeader") ?: "defaultContext",
            request = request,
            response = response)
}
