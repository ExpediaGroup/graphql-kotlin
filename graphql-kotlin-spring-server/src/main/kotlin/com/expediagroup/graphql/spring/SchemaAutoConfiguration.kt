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

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelNames
import com.expediagroup.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.extensions.print
import com.expediagroup.graphql.hooks.NoopSchemaGeneratorHooks
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.spring.extensions.toTopLevelObjects
import com.expediagroup.graphql.spring.operations.Mutation
import com.expediagroup.graphql.spring.operations.Query
import com.expediagroup.graphql.spring.operations.Subscription
import com.expediagroup.graphql.toSchema
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLSchema
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.util.Optional

/**
 * SpringBoot autoconfiguration for generating a non-federated GraphQL schema.
 */
@ConditionalOnProperty(value = ["graphql.federation.enabled"], havingValue = "false", matchIfMissing = true)
@Configuration
@Import(CommonSchemaConfiguration::class)
@EnableConfigurationProperties(GraphQLConfigurationProperties::class)
class SchemaAutoConfiguration {

    private val logger = LoggerFactory.getLogger(SchemaAutoConfiguration::class.java)

    @Bean
    @ConditionalOnMissingBean
    fun schemaConfig(
        config: GraphQLConfigurationProperties,
        topLevelNames: Optional<TopLevelNames>,
        hooks: Optional<SchemaGeneratorHooks>,
        dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider,
        codeRegistry: GraphQLCodeRegistry.Builder
    ): SchemaGeneratorConfig {
        val generatorHooks = hooks.orElse(NoopSchemaGeneratorHooks)
        return SchemaGeneratorConfig(
            supportedPackages = config.packages,
            topLevelNames = topLevelNames.orElse(TopLevelNames()),
            hooks = generatorHooks,
            dataFetcherFactoryProvider = dataFetcherFactoryProvider,
            codeRegistry = codeRegistry
        )
    }

    @Bean
    @ConditionalOnMissingBean
    fun schema(
        queries: Optional<List<Query>>,
        mutations: Optional<List<Mutation>>,
        subscriptions: Optional<List<Subscription>>,
        schemaConfig: SchemaGeneratorConfig
    ): GraphQLSchema {
        val schema = toSchema(
            config = schemaConfig,
            queries = queries.orElse(emptyList()).toTopLevelObjects(),
            mutations = mutations.orElse(emptyList()).toTopLevelObjects(),
            subscriptions = subscriptions.orElse(emptyList()).toTopLevelObjects()
        )

        logger.info("\n${schema.print()}")

        return schema
    }
}
