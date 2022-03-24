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

package com.expediagroup.graphql.examples.server.spring

import com.expediagroup.graphql.examples.server.spring.directives.CustomDirectiveWiringFactory
import com.expediagroup.graphql.examples.server.spring.exceptions.CustomDataFetcherExceptionHandler
import com.expediagroup.graphql.examples.server.spring.execution.CustomDataFetcherFactoryProvider
import com.expediagroup.graphql.examples.server.spring.execution.MySubscriptionHooks
import com.expediagroup.graphql.examples.server.spring.execution.SpringDataFetcherFactory
import com.expediagroup.graphql.examples.server.spring.hooks.CustomSchemaGeneratorHooks
import com.expediagroup.graphql.examples.server.spring.model.MyValueClass
import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.scalars.IDValueUnboxer
import com.expediagroup.graphql.server.spring.subscriptions.ApolloSubscriptionHooks
import graphql.execution.DataFetcherExceptionHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    @Bean
    fun wiringFactory() = CustomDirectiveWiringFactory()

    @Bean
    fun hooks(wiringFactory: KotlinDirectiveWiringFactory) = CustomSchemaGeneratorHooks(wiringFactory)

    @Bean
    fun dataFetcherFactoryProvider(
        springDataFetcherFactory: SpringDataFetcherFactory,
        applicationContext: ApplicationContext
    ) = CustomDataFetcherFactoryProvider(springDataFetcherFactory, applicationContext)

    @Bean
    fun dataFetcherExceptionHandler(): DataFetcherExceptionHandler = CustomDataFetcherExceptionHandler()

    @Bean
    fun apolloSubscriptionHooks(): ApolloSubscriptionHooks = MySubscriptionHooks()

    @Bean
    fun idValueUnboxer(): IDValueUnboxer = object : IDValueUnboxer() {
        override fun unbox(`object`: Any?): Any? = if (`object` is MyValueClass) {
            `object`.value
        } else {
            super.unbox(`object`)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
