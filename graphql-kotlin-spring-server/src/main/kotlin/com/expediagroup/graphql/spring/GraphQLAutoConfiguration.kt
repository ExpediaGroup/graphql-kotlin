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

import com.expediagroup.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.execution.SimpleKotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.spring.exception.KotlinDataFetcherExceptionHandler
import com.expediagroup.graphql.spring.execution.ContextWebFilter
import com.expediagroup.graphql.spring.execution.DataLoaderRegistryFactory
import com.expediagroup.graphql.spring.execution.EmptyDataLoaderRegistryFactory
import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import com.expediagroup.graphql.spring.execution.QueryHandler
import com.expediagroup.graphql.spring.execution.SimpleQueryHandler
import com.expediagroup.graphql.spring.execution.SpringDataFetcher
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.ExecutionIdProvider
import graphql.execution.SubscriptionExecutionStrategy
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.preparsed.PreparsedDocumentProvider
import graphql.schema.DataFetcherFactory
import graphql.schema.GraphQLSchema
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.Ordered
import java.util.Optional
import kotlin.reflect.KFunction

/**
 * Default order applied to Instrumentation beans.
 */
const val DEFAULT_INSTRUMENTATION_ORDER = 0

/**
 * SpringBoot auto-configuration that creates all beans required to start up reactive GraphQL web app.
 */
@Configuration
@Import(
    RoutesConfiguration::class,
    SchemaAutoConfiguration::class,
    FederatedSchemaAutoConfiguration::class,
    SubscriptionAutoConfiguration::class,
    PlaygroundAutoConfiguration::class
)
@EnableConfigurationProperties(GraphQLConfigurationProperties::class)
class GraphQLAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun dataFetcherFactoryProvider(objectMapper: ObjectMapper, applicationContext: ApplicationContext): KotlinDataFetcherFactoryProvider =
        object : SimpleKotlinDataFetcherFactoryProvider(objectMapper) {
            override fun functionDataFetcherFactory(target: Any?, kFunction: KFunction<*>): DataFetcherFactory<Any?> =
                DataFetcherFactory { SpringDataFetcher(target, kFunction, objectMapper, applicationContext) }
        }

    @Bean
    @ConditionalOnMissingBean
    fun exceptionHandler(): DataFetcherExceptionHandler = KotlinDataFetcherExceptionHandler()

    @Bean
    @ConditionalOnMissingBean
    fun graphQL(
        schema: GraphQLSchema,
        dataFetcherExceptionHandler: DataFetcherExceptionHandler,
        instrumentations: Optional<List<Instrumentation>>,
        executionIdProvider: Optional<ExecutionIdProvider>,
        preparsedDocumentProvider: Optional<PreparsedDocumentProvider>,
        config: GraphQLConfigurationProperties
    ): GraphQL {
        val graphQL = GraphQL.newGraphQL(schema)
            .queryExecutionStrategy(AsyncExecutionStrategy(dataFetcherExceptionHandler))
            .mutationExecutionStrategy(AsyncSerialExecutionStrategy(dataFetcherExceptionHandler))
            .subscriptionExecutionStrategy(SubscriptionExecutionStrategy(dataFetcherExceptionHandler))

        instrumentations.ifPresent { unordered ->
            if (unordered.size == 1) {
                graphQL.instrumentation(unordered.first())
            } else {
                val sorted = unordered.sortedBy {
                    if (it is Ordered) {
                        it.order
                    } else {
                        DEFAULT_INSTRUMENTATION_ORDER
                    }
                }
                graphQL.instrumentation(ChainedInstrumentation(sorted))
            }
        }
        executionIdProvider.ifPresent {
            graphQL.executionIdProvider(it)
        }
        preparsedDocumentProvider.ifPresent {
            graphQL.preparsedDocumentProvider(it)
        }

        return graphQL.build()
    }

    @Bean
    @ConditionalOnMissingBean
    fun dataLoaderRegistryFactory(): DataLoaderRegistryFactory = EmptyDataLoaderRegistryFactory()

    @Bean
    @ConditionalOnMissingBean
    fun graphQLQueryHandler(
        graphql: GraphQL,
        dataLoaderRegistryFactory: DataLoaderRegistryFactory
    ): QueryHandler = SimpleQueryHandler(graphql, dataLoaderRegistryFactory)

    @Bean
    @ConditionalOnMissingBean
    fun contextWebFilter(
        config: GraphQLConfigurationProperties,
        graphQLContextFactory: GraphQLContextFactory<*>
    ): ContextWebFilter<*> = ContextWebFilter(config, graphQLContextFactory)
}
