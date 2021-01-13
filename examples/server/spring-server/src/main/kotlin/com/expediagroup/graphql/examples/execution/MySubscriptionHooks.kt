package com.expediagroup.graphql.examples.execution

import com.expediagroup.graphql.examples.context.MySubscriptionGraphQLContext
import com.expediagroup.graphql.generator.execution.GraphQLContext
import com.expediagroup.graphql.server.spring.subscriptions.ApolloSubscriptionHooks
import kotlinx.coroutines.reactor.mono
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

/**
 * A simple implementation of Apollo Subscription Lifecycle Events.
 */
class MySubscriptionHooks : ApolloSubscriptionHooks {

    override fun onConnect(
        connectionParams: Map<String, String>,
        session: WebSocketSession,
        graphQLContext: GraphQLContext
    ): Mono<GraphQLContext> = mono {
        if (graphQLContext is MySubscriptionGraphQLContext) {
            val bearer = connectionParams["Authorization"] ?: "none"
            graphQLContext.subscriptionValue = bearer
        }
        graphQLContext
    }
}
