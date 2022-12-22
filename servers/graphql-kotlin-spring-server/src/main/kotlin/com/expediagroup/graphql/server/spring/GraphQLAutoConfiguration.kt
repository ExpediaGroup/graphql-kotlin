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

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * SpringBoot autoconfiguration that creates all beans required to start up reactive GraphQL server.
 * This is the top level configuration class that should be exposed and loaded for integration tests.
 */
@Configuration
@Import(
    GraphQLRoutesConfiguration::class,
    SubscriptionAutoConfiguration::class,
    SdlRouteConfiguration::class,
    PlaygroundRouteConfiguration::class,
    GraphiQLRouteConfiguration::class
)
class GraphQLAutoConfiguration
