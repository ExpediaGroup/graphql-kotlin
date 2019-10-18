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

package com.expediagroup.graphql.examples

import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.examples.datafetchers.CustomDataFetcherFactoryProvider
import com.expediagroup.graphql.examples.datafetchers.SpringDataFetcherFactory
import com.expediagroup.graphql.examples.directives.CustomDirectiveWiringFactory
import com.expediagroup.graphql.examples.exceptions.CustomDataFetcherExceptionHandler
import com.expediagroup.graphql.examples.extension.CustomSchemaGeneratorHooks
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import graphql.execution.DataFetcherExceptionHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import javax.validation.Validator

@SpringBootApplication
class Application {

    @Bean
    fun wiringFactory() = CustomDirectiveWiringFactory()

    @Bean
    fun hooks(validator: Validator, wiringFactory: KotlinDirectiveWiringFactory) =
        CustomSchemaGeneratorHooks(validator, wiringFactory)

    @Bean
    fun dataFetcherFactoryProvider(springDataFetcherFactory: SpringDataFetcherFactory, hooks: SchemaGeneratorHooks) =
        CustomDataFetcherFactoryProvider(springDataFetcherFactory, hooks)

    @Bean
    fun dataFetcherExceptionHandler(): DataFetcherExceptionHandler = CustomDataFetcherExceptionHandler()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
