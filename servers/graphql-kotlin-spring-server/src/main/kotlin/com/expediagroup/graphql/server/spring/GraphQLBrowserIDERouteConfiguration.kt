/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.server.spring

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.html

/**
 * Configuration for exposing the GraphQL Playground on a specific HTTP path
 */
@ConditionalOnProperty(value = ["graphql.playground.enabled"], havingValue = "true", matchIfMissing = true)
@Configuration
class GraphQLBrowserIDERouteConfiguration(
    private val config: GraphQLConfigurationProperties,
    @Value("classpath:/graphql-playground.html") private val playgroundHtml: Resource,
    @Value("classpath:/graphql-graphiql.html") private val graphiQLHtml: Resource,
    @Value("\${spring.webflux.base-path:#{null}}") private val contextPath: String?
) {

    private val body = config.browserIDE.ide.let { ide ->
        val ideHtml = when (ide) {
            GraphQLConfigurationProperties.GraphQLBrowserIDE.PLAYGROUND -> playgroundHtml
            GraphQLConfigurationProperties.GraphQLBrowserIDE.GRAPHIQL -> graphiQLHtml
        }
        ideHtml.inputStream.bufferedReader().use { reader ->
            val graphQLEndpoint = if (contextPath.isNullOrBlank()) config.endpoint else "$contextPath/${config.endpoint}"
            val subscriptionsEndpoint = if (contextPath.isNullOrBlank()) config.subscriptions.endpoint else "$contextPath/${config.subscriptions.endpoint}"

            reader.readText()
                .replace("\${graphQLEndpoint}", graphQLEndpoint)
                .replace("\${subscriptionsEndpoint}", subscriptionsEndpoint)
        }
    }

    @Bean
    fun playgroundRoute() = coRouter {
        GET(config.browserIDE.endpoint) {
            ok().html().bodyValueAndAwait(body)
        }
    }
}
