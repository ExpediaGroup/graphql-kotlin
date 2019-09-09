/*
 * Copyright 2019 Expedia Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
