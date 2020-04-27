/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.spring.execution

import com.expediagroup.graphql.execution.GraphQLContext
import com.expediagroup.graphql.spring.GraphQLConfigurationProperties
import kotlinx.coroutines.reactor.mono
import org.springframework.core.Ordered
import org.springframework.http.server.PathContainer
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.web.util.pattern.PathPatternParser
import reactor.core.publisher.Mono

/**
 * [org.springframework.core.Ordered] value used for the [ContextWebFilter] order in which it will be applied to the incoming requests.
 * Smaller value take higher precedence.
 */
const val GRAPHQL_CONTEXT_FILTER_ODER = 0

/**
 * Default web filter that populates GraphQL context in the reactor subscriber context.
 */
open class ContextWebFilter<out T : GraphQLContext>(config: GraphQLConfigurationProperties, private val contextFactory: GraphQLContextFactory<T>) : WebFilter, Ordered {
    private val graphQLRoute = enforceAbsolutePath(config.endpoint)
    private val subscriptionsRoute = enforceAbsolutePath(config.subscriptions.endpoint)
    private val parser = getParser()
    private val graphQLRoutePattern = parser.parse(graphQLRoute)
    private val subscriptionsRoutePattern = parser.parse(subscriptionsRoute)

    @Suppress("ForbiddenVoid")
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> =
        if (isApplicable(exchange.request.uri.path)) {
            mono {
                contextFactory.generateContext(exchange.request, exchange.response)
            }.flatMap { graphQLContext ->
                chain.filter(exchange).subscriberContext { it.put(GRAPHQL_CONTEXT_KEY, graphQLContext) }
            }
        } else {
            chain.filter(exchange)
        }

    override fun getOrder(): Int = GRAPHQL_CONTEXT_FILTER_ODER

    open fun isApplicable(path: String): Boolean {
        val parsedPath = PathContainer.parsePath(path)
        return graphQLRoutePattern.matches(parsedPath) || subscriptionsRoutePattern.matches(parsedPath)
    }

    private fun enforceAbsolutePath(path: String) = if (path.startsWith("/")) { path } else { "/$path" }

    private fun getParser(): PathPatternParser {
        val parser = PathPatternParser()
        parser.isCaseSensitive = false
        return parser
    }
}
