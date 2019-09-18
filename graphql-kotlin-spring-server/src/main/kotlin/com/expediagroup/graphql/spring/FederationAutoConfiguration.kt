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

import com.expediagroup.graphql.extensions.print
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry
import com.expediagroup.graphql.federation.toFederatedSchema
import com.expediagroup.graphql.spring.operations.Mutation
import com.expediagroup.graphql.spring.operations.Query
import com.expediagroup.graphql.spring.operations.Subscription
import graphql.schema.GraphQLSchema
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Optional

/**
 * SpringBoot autoconfiguration for generating Federated GraphQL schema.
 */
@ConditionalOnProperty(value = ["graphql.federation.enabled"], havingValue = "true")
@Configuration
class FederationAutoConfiguration {

    private val logger = LoggerFactory.getLogger(FederationAutoConfiguration::class.java)

    @Bean
    @ConditionalOnMissingBean
    fun federatedTypeRegistry(): FederatedTypeRegistry = FederatedTypeRegistry(emptyMap())

    @Bean
    @ConditionalOnMissingBean
    fun federatedSchemaConfig(config: GraphQLConfigurationProperties, registry: FederatedTypeRegistry): FederatedSchemaGeneratorConfig = FederatedSchemaGeneratorConfig(
        supportedPackages = config.packages,
        hooks = FederatedSchemaGeneratorHooks(registry)
    )

    @Bean
    @ConditionalOnMissingBean
    fun schema(
        queries: Optional<List<Query>>,
        mutations: Optional<List<Mutation>>,
        subscriptions: Optional<List<Subscription>>,
        schemaConfig: FederatedSchemaGeneratorConfig
    ): GraphQLSchema {
        val schema = toFederatedSchema(
            config = schemaConfig,
            queries = queries.orElse(emptyList()).toTopLevelObjects(),
            mutations = mutations.orElse(emptyList()).toTopLevelObjects(),
            subscriptions = subscriptions.orElse(emptyList()).toTopLevelObjects()
        )

        logger.info(schema.print())
        return schema
    }
}
