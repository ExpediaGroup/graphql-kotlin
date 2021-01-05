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

import com.expediagroup.graphql.execution.FlowSubscriptionExecutionStrategy
import com.expediagroup.graphql.server.execution.DataLoaderRegistryFactory
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
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
 * configuraiton and creates the GraphQL schema object and request handler.
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
        instrumentations: Optional<List<Instrumentation>>,
        executionIdProvider: Optional<ExecutionIdProvider>,
        preparsedDocumentProvider: Optional<PreparsedDocumentProvider>,
        config: GraphQLConfigurationProperties
    ): GraphQL {
        val graphQL = GraphQL.newGraphQL(schema)
            .queryExecutionStrategy(AsyncExecutionStrategy(dataFetcherExceptionHandler))
            .mutationExecutionStrategy(AsyncSerialExecutionStrategy(dataFetcherExceptionHandler))
            .subscriptionExecutionStrategy(FlowSubscriptionExecutionStrategy(dataFetcherExceptionHandler))

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
    fun graphQLRequestHandler(
        graphql: GraphQL,
        dataLoaderRegistryFactory: DataLoaderRegistryFactory
    ) = GraphQLRequestHandler(graphql, dataLoaderRegistryFactory)
}
