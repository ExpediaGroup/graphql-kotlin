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

import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation
import com.expediagroup.graphql.TopLevelNames
import com.expediagroup.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.extensions.print
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry
import com.expediagroup.graphql.federation.toFederatedSchema
import com.expediagroup.graphql.spring.execution.DefaultFederatedContextFactory
import com.expediagroup.graphql.spring.execution.FederatedGraphQLContextFactory
import com.expediagroup.graphql.spring.extensions.toTopLevelObjects
import com.expediagroup.graphql.spring.operations.Mutation
import com.expediagroup.graphql.spring.operations.Query
import com.expediagroup.graphql.spring.operations.Subscription
import graphql.schema.GraphQLSchema
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * SpringBoot autoconfiguration for generating Federated GraphQL schema.
 */
@ConditionalOnProperty(value = ["graphql.federation.enabled"], havingValue = "true")
@Configuration
class FederatedSchemaAutoConfiguration(
    private val config: GraphQLConfigurationProperties
) {

    private val logger = LoggerFactory.getLogger(FederatedSchemaAutoConfiguration::class.java)

    @Bean
    @ConditionalOnMissingBean
    fun federatedTypeRegistry(): FederatedTypeRegistry = FederatedTypeRegistry(emptyMap())

    @Bean
    @ConditionalOnMissingBean
    fun federatedSchemaGeneratorHooks(registry: FederatedTypeRegistry): FederatedSchemaGeneratorHooks = FederatedSchemaGeneratorHooks(registry)

    @Bean
    @ConditionalOnMissingBean
    fun federatedSchemaConfig(
        hooks: FederatedSchemaGeneratorHooks,
        topLevelNames: Optional<TopLevelNames>,
        dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider
    ): FederatedSchemaGeneratorConfig = FederatedSchemaGeneratorConfig(
        supportedPackages = config.packages,
        topLevelNames = topLevelNames.orElse(TopLevelNames()),
        hooks = hooks,
        dataFetcherFactoryProvider = dataFetcherFactoryProvider,
        introspectionEnabled = config.introspection.enabled
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

        logger.info("\n${schema.print()}")

        return schema
    }

    /**
     * Instrumentation is automatically added to the schema if it is registered as a spring component.
     * This registers the federation tracing instrumentation for federated services.
     */
    @Bean
    @ConditionalOnProperty(value = ["graphql.federation.tracing.enabled"], havingValue = "true", matchIfMissing = true)
    fun federatedTracing(): FederatedTracingInstrumentation =
        FederatedTracingInstrumentation(FederatedTracingInstrumentation.Options(config.federation.tracing.debug))

    /**
     * Federation requires we use a different context for the tracing so we must use the
     * specific interface factory.
     */
    @Bean
    @ConditionalOnMissingBean
    fun graphQLContextFactory(): FederatedGraphQLContextFactory<*> = DefaultFederatedContextFactory
}
