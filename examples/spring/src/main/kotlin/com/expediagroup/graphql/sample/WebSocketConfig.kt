package com.expediagroup.graphql.sample

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping

@Configuration
class WebSocketConfig {

    @Bean
    fun handlerMapping(subscriptionHandler: SubscriptionHandler) : HandlerMapping {
        val handlerMapping = SimpleUrlHandlerMapping()

        handlerMapping.urlMap = mapOf("/subscriptions" to subscriptionHandler)
        handlerMapping.order = HIGHEST_PRECEDENCE

        return handlerMapping
    }
}
