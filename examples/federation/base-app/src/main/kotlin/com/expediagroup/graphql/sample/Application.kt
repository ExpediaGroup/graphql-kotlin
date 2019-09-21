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

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry
import com.expediagroup.graphql.sample.datafetchers.CustomDataFetcherFactoryProvider
import com.expediagroup.graphql.sample.datafetchers.SpringDataFetcherFactory
import com.expediagroup.graphql.sample.directives.CustomDirectiveWiringFactory
import com.expediagroup.graphql.sample.exceptions.CustomDataFetcherExceptionHandler
import com.expediagroup.graphql.sample.extension.CustomSchemaGeneratorHooks
import graphql.execution.DataFetcherExceptionHandler
import javax.validation.Validator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    @Bean
    fun wiringFactory() = CustomDirectiveWiringFactory()

    @Bean
    fun federatedTypeRegistry() = FederatedTypeRegistry()

    @Bean
    fun hooks(validator: Validator, wiringFactory: KotlinDirectiveWiringFactory, federatedTypeRegistry: FederatedTypeRegistry) =
        CustomSchemaGeneratorHooks(validator, wiringFactory, federatedTypeRegistry)

    @Bean
    fun dataFetcherFactoryProvider(springDataFetcherFactory: SpringDataFetcherFactory, hooks: FederatedSchemaGeneratorHooks) =
        CustomDataFetcherFactoryProvider(springDataFetcherFactory, hooks)

    @Bean
    fun schemaConfig(hooks: FederatedSchemaGeneratorHooks, dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider): FederatedSchemaGeneratorConfig = FederatedSchemaGeneratorConfig(
        supportedPackages = listOf("com.expediagroup"),
        hooks = hooks,
        dataFetcherFactoryProvider = dataFetcherFactoryProvider
    )

    @Bean
    fun dataFetcherExceptionHandler(): DataFetcherExceptionHandler = CustomDataFetcherExceptionHandler()
}
@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
