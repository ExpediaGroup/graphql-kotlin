package com.expediagroup.graphql.examples.execution

import com.expediagroup.graphql.examples.context.MyGraphQLContext
import com.expediagroup.graphql.spring.execution.ApolloSubscriptionHooks
import kotlinx.coroutines.reactor.mono
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

/**
 * A simple implementation of Apollo Subscription Lifecycle Events.
 */
open class MySubscriptionHooks :
    ApolloSubscriptionHooks {
    override fun onConnect(
        connectionParams: Map<String, String>,
        session: WebSocketSession,
        graphQLContext: Any?
    ): Mono<Unit> = mono {
        val bearer = connectionParams["Authorization"] ?: "none"
        val context = graphQLContext as? MyGraphQLContext
        context?.subscriptionValue = bearer
    }
}
