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

import com.expediagroup.graphql.apq.cache.AutomaticPersistedQueriesCache
import com.expediagroup.graphql.apq.cache.DefaultAutomaticPersistedQueriesCache
import com.expediagroup.graphql.apq.provider.AutomaticPersistedQueriesProvider
import com.expediagroup.graphql.generator.execution.FlowSubscriptionExecutionStrategy
import com.expediagroup.graphql.generator.scalars.IDValueUnboxer
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.dataloader.instrumentation.level.DataLoaderLevelDispatchedInstrumentation
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.DataLoaderSyncExecutionExhaustedInstrumentation
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.spring.execution.DefaultSpringGraphQLContextFactory
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContextFactory
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLRequestParser
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLServer
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.ExecutionIdProvider
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.preparsed.PreparsedDocumentProvider
import graphql.schema.GraphQLSchema
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.Ordered
import java.util.Optional

/**
 * Default order applied to Instrumentation beans.
 */
const val DEFAULT_INSTRUMENTATION_ORDER = 0

/**
 * Configuration class that loads both the federated and non-federation
 * configuration and creates the GraphQL schema object and request handler.
 *
 * This config can then be used by all Spring specific configuration classes
 * to handle incoming requests from HTTP routes or subscriptions and send them
 * to the schema object.
 */
@Configuration
@Import(
    NonFederatedSchemaAutoConfiguration::class,
    FederatedSchemaAutoConfiguration::class
)
class GraphQLSchemaConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun graphQL(
        schema: GraphQLSchema,
        dataFetcherExceptionHandler: DataFetcherExceptionHandler,
        providedInstrumentations: Optional<List<Instrumentation>>,
        executionIdProvider: Optional<ExecutionIdProvider>,
        preparsedDocumentProvider: Optional<PreparsedDocumentProvider>,
        config: GraphQLConfigurationProperties,
        idValueUnboxer: IDValueUnboxer
    ): GraphQL {
        val graphQLBuilder = GraphQL.newGraphQL(schema)
            .queryExecutionStrategy(AsyncExecutionStrategy(dataFetcherExceptionHandler))
            .mutationExecutionStrategy(AsyncSerialExecutionStrategy(dataFetcherExceptionHandler))
            .subscriptionExecutionStrategy(FlowSubscriptionExecutionStrategy(dataFetcherExceptionHandler))
            .valueUnboxer(idValueUnboxer)
            .also { builder ->
                executionIdProvider.ifPresent(builder::executionIdProvider)
                preparsedDocumentProvider.ifPresent(builder::preparsedDocumentProvider)

                val instrumentations = mutableListOf<Instrumentation>()
                if (config.batching.enabled) {
                    builder.doNotAddDefaultInstrumentations()
                    instrumentations.add(
                        when (config.batching.strategy) {
                            GraphQLConfigurationProperties.BatchingStrategy.LEVEL_DISPATCHED -> DataLoaderLevelDispatchedInstrumentation()
                            GraphQLConfigurationProperties.BatchingStrategy.SYNC_EXHAUSTION -> DataLoaderSyncExecutionExhaustedInstrumentation()
                        }
                    )
                }

                providedInstrumentations.ifPresent { unorderedInstrumentations ->
                    instrumentations.addAll(
                        unorderedInstrumentations.sortedBy { instrumentation ->
                            when (instrumentation) {
                                is Ordered -> instrumentation.order
                                else -> DEFAULT_INSTRUMENTATION_ORDER
                            }
                        }
                    )
                }

                builder.instrumentation(ChainedInstrumentation(instrumentations))
            }

        return graphQLBuilder.build()
    }

    @Bean
    @ConditionalOnProperty(
        name = ["graphql.automaticPersistedQueries.enabled"],
        havingValue = "true"
    )
    fun preparsedDocumentProvider(
        providedPersistedQueriesCache: Optional<AutomaticPersistedQueriesCache>
    ): AutomaticPersistedQueriesProvider = AutomaticPersistedQueriesProvider(
        providedPersistedQueriesCache.orElse(DefaultAutomaticPersistedQueriesCache())
    )

    @Bean
    @ConditionalOnMissingBean
    fun idValueUnboxer(): IDValueUnboxer = IDValueUnboxer()

    @Bean
    @ConditionalOnMissingBean
    fun springGraphQLRequestParser(objectMapper: ObjectMapper): SpringGraphQLRequestParser =
        SpringGraphQLRequestParser(objectMapper)

    @Bean
    @ConditionalOnMissingBean
    fun springGraphQLContextFactory(): SpringGraphQLContextFactory =
        DefaultSpringGraphQLContextFactory()

    @Bean
    @ConditionalOnMissingBean
    fun graphQLRequestHandler(
        graphql: GraphQL,
        dataLoaderRegistryFactory: KotlinDataLoaderRegistryFactory
    ): GraphQLRequestHandler = GraphQLRequestHandler(
        graphql,
        dataLoaderRegistryFactory
    )

    @Bean
    @ConditionalOnMissingBean
    fun springGraphQLServer(
        requestParser: SpringGraphQLRequestParser,
        contextFactory: SpringGraphQLContextFactory,
        requestHandler: GraphQLRequestHandler
    ): SpringGraphQLServer = SpringGraphQLServer(
        requestParser,
        contextFactory,
        requestHandler
    )
}
