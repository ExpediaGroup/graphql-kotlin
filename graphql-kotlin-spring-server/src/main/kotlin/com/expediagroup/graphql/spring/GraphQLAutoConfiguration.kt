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

import com.expediagroup.graphql.spring.exception.KotlinDataFetcherExceptionHandler
import com.expediagroup.graphql.spring.execution.ContextWebFilter
import com.expediagroup.graphql.spring.execution.EmptyContextFactory
import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import com.expediagroup.graphql.spring.execution.QueryHandler
import com.expediagroup.graphql.spring.execution.SimpleQueryHandler
import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.ExecutionIdProvider
import graphql.execution.SubscriptionExecutionStrategy
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.preparsed.PreparsedDocumentProvider
import graphql.schema.GraphQLSchema
import org.dataloader.DataLoaderRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.server.WebFilter
import java.util.Optional

/**
 * SpringBoot auto-configuration that creates all beans required to start up reactive GraphQL web app.
 */
@Configuration
@Import(
    RoutesConfiguration::class,
    SchemaAutoConfiguration::class,
    FederationAutoConfiguration::class,
    SubscriptionAutoConfiguration::class,
    PlaygroundAutoConfiguration::class
)
@EnableConfigurationProperties(GraphQLConfigurationProperties::class)
class GraphQLAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun exceptionHandler(): DataFetcherExceptionHandler = KotlinDataFetcherExceptionHandler()

    @Bean
    @ConditionalOnMissingBean
    fun graphQL(
        schema: GraphQLSchema,
        dataFetcherExceptionHandler: DataFetcherExceptionHandler,
        instrumentation: Optional<Instrumentation>,
        executionIdProvider: Optional<ExecutionIdProvider>,
        preparsedDocumentProvider: Optional<PreparsedDocumentProvider>
    ): GraphQL {
        val graphQL = GraphQL.newGraphQL(schema)
            .queryExecutionStrategy(AsyncExecutionStrategy(dataFetcherExceptionHandler))
            .mutationExecutionStrategy(AsyncSerialExecutionStrategy(dataFetcherExceptionHandler))
            .subscriptionExecutionStrategy(SubscriptionExecutionStrategy(dataFetcherExceptionHandler))

        instrumentation.ifPresent {
            graphQL.instrumentation(it)
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
    fun dataLoaderRegistry(): DataLoaderRegistry = DataLoaderRegistry()

    @Bean
    @ConditionalOnMissingBean
    fun graphQLQueryHandler(graphql: GraphQL, dataLoaderRegistry: DataLoaderRegistry): QueryHandler = SimpleQueryHandler(graphql, dataLoaderRegistry)

    @Bean
    @ConditionalOnMissingBean
    fun graphQLContextFactory(): GraphQLContextFactory<*> = EmptyContextFactory

    @Bean
    fun contextWebFilter(
        config: GraphQLConfigurationProperties,
        graphQLContextFactory: GraphQLContextFactory<*>
    ): WebFilter = ContextWebFilter(config, graphQLContextFactory)
}
