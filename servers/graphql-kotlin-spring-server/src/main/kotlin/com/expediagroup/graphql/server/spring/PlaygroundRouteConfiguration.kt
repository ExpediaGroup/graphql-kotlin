/*
 * Copyright 2022 Expedia, Inc
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
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.html

/**
 * Configuration for exposing the GraphQL Playground on a specific HTTP path
 */
@ConditionalOnProperty(value = ["graphql.playground.enabled"], havingValue = "true", matchIfMissing = false)
@Configuration
class PlaygroundRouteConfiguration(
    private val config: GraphQLConfigurationProperties,
    @Value("classpath:/graphql-playground.html") private val html: Resource,
    @Value("\${spring.webflux.base-path:#{null}}") private val contextPath: String?
) {
    @Bean
    fun playgroundRoute(): RouterFunction<ServerResponse> = coRouter {
        GET(config.playground.endpoint) {
            ok().html().bodyValueAndAwait(
                html.inputStream.bufferedReader().use { reader ->
                    reader.readText()
                        .replace("\${graphQLEndpoint}", if (contextPath.isNullOrBlank()) config.endpoint else "$contextPath/${config.endpoint}")
                        .replace("\${subscriptionsEndpoint}", if (contextPath.isNullOrBlank()) config.subscriptions.endpoint else "$contextPath/${config.subscriptions.endpoint}")
                }
            )
        }
    }
}
