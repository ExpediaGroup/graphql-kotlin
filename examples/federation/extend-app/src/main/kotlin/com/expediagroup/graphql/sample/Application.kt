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

package com.expediagroup.graphql.sample

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.extensions.print
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry
import com.expediagroup.graphql.federation.toFederatedSchema
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.sample.datafetchers.CustomDataFetcherFactoryProvider
import com.expediagroup.graphql.sample.datafetchers.SpringDataFetcherFactory
import com.expediagroup.graphql.sample.exceptions.CustomDataFetcherExceptionHandler
import com.expediagroup.graphql.sample.extend.widgetResolver
import com.expediagroup.graphql.sample.query.Query
import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.SubscriptionExecutionStrategy
import graphql.schema.GraphQLSchema
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    private val logger = LoggerFactory.getLogger(Application::class.java)

    @Bean
    fun federatedTypeRegistry() = FederatedTypeRegistry(mapOf("Widget" to widgetResolver))

    @Bean
    fun hooks(registry: FederatedTypeRegistry) = FederatedSchemaGeneratorHooks(registry)

    @Bean
    fun dataFetcherFactoryProvider(springDataFetcherFactory: SpringDataFetcherFactory, hooks: SchemaGeneratorHooks) =
        CustomDataFetcherFactoryProvider(springDataFetcherFactory, hooks)

    @Bean
    fun schemaConfig(hooks: FederatedSchemaGeneratorHooks, dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider) = FederatedSchemaGeneratorConfig(
        supportedPackages = listOf("com.expediagroup"),
        hooks = hooks,
        dataFetcherFactoryProvider = dataFetcherFactoryProvider
    )

    @Bean
    fun schema(
        queries: List<Query>,
        schemaConfig: FederatedSchemaGeneratorConfig
    ): GraphQLSchema {
        fun List<Any>.toTopLevelObjects() = this.map {
            TopLevelObject(it)
        }

        val schema = toFederatedSchema(
            config = schemaConfig,
            queries = queries.toTopLevelObjects()
        )

        logger.info(schema.print())

        return schema
    }

    @Bean
    fun dataFetcherExceptionHandler(): DataFetcherExceptionHandler = CustomDataFetcherExceptionHandler()

    @Bean
    fun graphQL(
        schema: GraphQLSchema,
        dataFetcherExceptionHandler: DataFetcherExceptionHandler
    ): GraphQL = GraphQL.newGraphQL(schema)
        .queryExecutionStrategy(AsyncExecutionStrategy(dataFetcherExceptionHandler))
        .mutationExecutionStrategy(AsyncSerialExecutionStrategy(dataFetcherExceptionHandler))
        .subscriptionExecutionStrategy(SubscriptionExecutionStrategy(dataFetcherExceptionHandler))
        .build()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
