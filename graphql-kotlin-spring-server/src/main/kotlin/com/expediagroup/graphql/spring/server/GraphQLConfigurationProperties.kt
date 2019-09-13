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

package com.expediagroup.graphql.spring.server

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * [ConfigurationProperties] bean that defines supported GraphQL configuration options.
 */
@ConfigurationProperties("graphql")
class GraphQLConfigurationProperties {
    var endpoint: String = "graphql"
    var packages: List<String> = emptyList()
    var federation: FederationConfigurationProperties = FederationConfigurationProperties()
    var subscriptions: SubscriptionConfigurationProperties = SubscriptionConfigurationProperties()
}

/**
 * Apollo Federation configuration properties.
 */
class FederationConfigurationProperties {
    var enabled: Boolean = false
}

/**
 * GraphQL subscription configuration properties.
 */
class SubscriptionConfigurationProperties {
    var endpoint: String = "subscriptions"
}
