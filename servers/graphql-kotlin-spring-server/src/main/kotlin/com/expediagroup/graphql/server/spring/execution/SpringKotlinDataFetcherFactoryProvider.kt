/*
 * Copyright 2025 Expedia, Inc
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

package com.expediagroup.graphql.server.spring.execution

import com.expediagroup.graphql.generator.execution.SimpleKotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.execution.SimpleSingletonKotlinDataFetcherFactoryProvider
import graphql.schema.DataFetcherFactory
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * This provides a wrapper around the [SimpleKotlinDataFetcherFactoryProvider] to call the [SpringDataFetcher] on functions.
 * This allows you to use Spring beans as function arguments, and they will be populated by the data fetcher.
 */
open class SpringKotlinDataFetcherFactoryProvider(
    private val applicationContext: ApplicationContext
) : SimpleKotlinDataFetcherFactoryProvider() {
    override fun functionDataFetcherFactory(target: Any?, kFunction: KFunction<*>): DataFetcherFactory<Any?> =
        DataFetcherFactory { SpringDataFetcher(target, kFunction, applicationContext) }
}

/**
 * This provides a wrapper around the [SimpleSingletonKotlinDataFetcherFactoryProvider] to call the [SpringDataFetcher] on functions.
 * This allows you to use Spring beans as function arguments, and they will be populated by the data fetcher.
 */
open class SpringSingletonKotlinDataFetcherFactoryProvider(
    private val applicationContext: ApplicationContext
) : SimpleSingletonKotlinDataFetcherFactoryProvider() {
    override fun functionDataFetcherFactory(target: Any?, kFunction: KFunction<*>): DataFetcherFactory<Any?> =
        DataFetcherFactory { SpringDataFetcher(target, kFunction, applicationContext) }
}
