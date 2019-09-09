/*
 * Copyright 2019 Expedia Group, Inc.
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

package com.expediagroup.graphql.sample.context

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * Simple WebFilter that creates custom [MyGraphQLContext] and adds its to the SubscriberContext.
 */
@Component
class MyGraphQLContextWebFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val myValue = exchange.request.headers.getFirst("MyHeader") ?: "defaultContext"
        val customContext = MyGraphQLContext(
            myCustomValue = myValue,
            request = exchange.request,
            response = exchange.response)
        return chain.filter(exchange).subscriberContext { it.put("graphQLContext", customContext) }
    }
}
