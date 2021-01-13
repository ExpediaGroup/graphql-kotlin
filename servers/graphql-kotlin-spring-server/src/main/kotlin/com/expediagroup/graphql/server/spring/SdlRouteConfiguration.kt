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

import com.expediagroup.graphql.generator.extensions.print
import graphql.schema.GraphQLSchema
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

/**
 * Configuration to expose the SDL of the schema on on specific HTTP path
 */
@Configuration
@Import(GraphQLSchemaConfiguration::class)
class SdlRouteConfiguration(
    private val config: GraphQLConfigurationProperties,
    schema: GraphQLSchema
) {

    private val sdl = schema.print()

    @Bean
    @ConditionalOnProperty(value = ["graphql.sdl.enabled"], havingValue = "true", matchIfMissing = true)
    fun sdlRoute() = coRouter {
        GET(config.sdl.endpoint) {
            ok().contentType(MediaType.TEXT_PLAIN).bodyValueAndAwait(sdl)
        }
    }
}
