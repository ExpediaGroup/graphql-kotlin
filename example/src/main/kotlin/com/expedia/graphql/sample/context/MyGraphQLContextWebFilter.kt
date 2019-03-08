package com.expedia.graphql.sample.context

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * Simple WebFilter that creates custom [MyGraphQLContext] and adds its to the SubscriberContext.
 */
@Component
class MyGraphQLContextWebFilter: WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val myValue = exchange.request.headers.getFirst("MyHeader") ?: "defaultContext"
        val customContext = MyGraphQLContext(
                myCustomValue = myValue,
                request = exchange.request,
                response = exchange.response)
        return chain.filter(exchange).subscriberContext { it.put("graphQLContext", customContext) }
    }
}