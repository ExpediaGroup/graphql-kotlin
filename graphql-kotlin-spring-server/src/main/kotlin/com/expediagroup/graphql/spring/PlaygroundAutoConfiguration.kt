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

package com.expediagroup.graphql.spring

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
 * SpringBoot auto configuration for generating Playground Service.
 */
@ConditionalOnProperty(value = ["graphql.playground.enabled"], havingValue = "true", matchIfMissing = true)
@Configuration
class PlaygroundAutoConfiguration(
    private val config: GraphQLConfigurationProperties,
    @Value("classpath:/graphql-playground.html") private val playgroundHtml: Resource
) {

    private val body = playgroundHtml.inputStream.bufferedReader().use { reader ->
        reader.readText()
            .replace("\${graphQLEndpoint}", config.endpoint)
            .replace("\${subscriptionsEndpoint}", config.subscriptions.endpoint)
    }

    @Bean
    fun playgroundRoute() = coRouter {
        GET(config.playground.endpoint) {
            ok().html().bodyValueAndAwait(body)
        }
    }
}
