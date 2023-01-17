/*
 * Copyright 2023 Expedia, Inc
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

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelNames
import com.expediagroup.graphql.generator.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.hooks.NoopSchemaGeneratorHooks
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.Schema
import com.expediagroup.graphql.server.operations.Mutation
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.operations.Subscription
import com.expediagroup.graphql.server.spring.extensions.toTopLevelObject
import com.expediagroup.graphql.server.spring.extensions.toTopLevelObjects
import graphql.schema.GraphQLSchema
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.util.Optional

/**
 * SpringBoot autoconfiguration for generating a non-federated GraphQL schema object.
 * This will override the beans in [FederatedSchemaAutoConfiguration] if federation is disabled.
 */
@ConditionalOnProperty(value = ["graphql.federation.enabled"], havingValue = "false", matchIfMissing = true)
@Configuration
@Import(GraphQLExecutionConfiguration::class)
class NonFederatedSchemaAutoConfiguration(
    private val config: GraphQLConfigurationProperties
) {

    private val logger = LoggerFactory.getLogger(NonFederatedSchemaAutoConfiguration::class.java)

    @Bean
    @ConditionalOnMissingBean
    fun schemaConfig(
        topLevelNames: Optional<TopLevelNames>,
        hooks: Optional<SchemaGeneratorHooks>,
        dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider
    ): SchemaGeneratorConfig = SchemaGeneratorConfig(
        supportedPackages = config.packages,
        topLevelNames = topLevelNames.orElse(TopLevelNames()),
        hooks = hooks.orElse(NoopSchemaGeneratorHooks),
        dataFetcherFactoryProvider = dataFetcherFactoryProvider,
        introspectionEnabled = config.introspection.enabled
    )

    @Bean
    @ConditionalOnMissingBean
    fun schema(
        queries: Optional<List<Query>>,
        mutations: Optional<List<Mutation>>,
        subscriptions: Optional<List<Subscription>>,
        schemaConfig: SchemaGeneratorConfig,
        schemaObject: Optional<Schema>
    ): GraphQLSchema = toSchema(
        config = schemaConfig,
        queries = queries.orElse(emptyList()).toTopLevelObjects(),
        mutations = mutations.orElse(emptyList()).toTopLevelObjects(),
        subscriptions = subscriptions.orElse(emptyList()).toTopLevelObjects(),
        schemaObject = schemaObject.orElse(null)?.toTopLevelObject()
    ).also { schema ->
        if (config.printSchema) {
            logger.info("\n${schema.print()}")
        }
    }
}
