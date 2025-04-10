/*
 * Copyright 2022 Expedia, Inc
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

import com.expediagroup.graphql.generator.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.expediagroup.graphql.server.spring.execution.SpringKotlinDataFetcherFactoryProvider
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.SimpleDataFetcherExceptionHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.util.Optional

/**
 * The root configuration class that other configurations can import to get the basic
 * beans required to then create an executable GraphQL schema object.
 */
@Configuration
@EnableConfigurationProperties(GraphQLConfigurationProperties::class)
@Import(JacksonAutoConfiguration::class)
class GraphQLExecutionConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun dataFetcherFactoryProvider(applicationContext: ApplicationContext): KotlinDataFetcherFactoryProvider =
        SpringKotlinDataFetcherFactoryProvider(applicationContext)

    @Bean
    @ConditionalOnMissingBean
    fun exceptionHandler(): DataFetcherExceptionHandler = SimpleDataFetcherExceptionHandler()

    @Bean
    @ConditionalOnMissingBean
    fun dataLoaderRegistryFactory(
        dataLoaders: Optional<List<KotlinDataLoader<*, *>>>
    ): KotlinDataLoaderRegistryFactory =
        KotlinDataLoaderRegistryFactory(
            dataLoaders.orElse(emptyList())
        )
}
