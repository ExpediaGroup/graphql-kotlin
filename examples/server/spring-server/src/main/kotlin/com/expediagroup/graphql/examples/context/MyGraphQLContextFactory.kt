package com.expediagroup.graphql.examples.context

import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContextFactory
import com.expediagroup.graphql.server.spring.subscriptions.SpringSubscriptionGraphQLContextFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.socket.WebSocketSession

/**
 * [GraphQLContextFactory] that generates [MyGraphQLContext] that will be available when processing GraphQL requests.
 */
@Component
class MyGraphQLContextFactory : SpringGraphQLContextFactory<MyGraphQLContext>() {

    override fun generateContext(request: ServerRequest): MyGraphQLContext = MyGraphQLContext(
        myCustomValue = request.headers().firstHeader("MyHeader") ?: "defaultContext",
        request = request
    )
}

@Component
class MySubscriptionGraphQLContextFactory : SpringSubscriptionGraphQLContextFactory<MySubscriptionGraphQLContext>() {

    override fun generateContext(request: WebSocketSession): MySubscriptionGraphQLContext = MySubscriptionGraphQLContext(
        request = request,
        subscriptionValue = null
    )
}
