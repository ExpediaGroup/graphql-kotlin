/*
 * Copyright 2020 Expedia, Inc
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

import org.springframework.context.annotation.Import

/**
 * Top level class to import all the beans of graphql-kotlin-spring-server into the application context.
 * This is set in the spring.factories as the enabled auto configuration class.
 */
@Import(
    RoutesConfiguration::class,
    PlaygroundAutoConfiguration::class,
    SubscriptionAutoConfiguration::class
)
class GraphQLSpringAutoConfiguration
