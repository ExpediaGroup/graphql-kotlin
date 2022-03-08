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

package com.expediagroup.graphql.examples.server.spring.execution

import com.expediagroup.graphql.generator.execution.SimpleKotlinDataFetcherFactoryProvider
import graphql.schema.DataFetcherFactory
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

/**
 * Custom DataFetcherFactory provider that returns custom Spring based DataFetcherFactory for resolving lateinit properties.
 */
class CustomDataFetcherFactoryProvider(
    private val springDataFetcherFactory: SpringDataFetcherFactory,
    private val applicationContext: ApplicationContext
) : SimpleKotlinDataFetcherFactoryProvider() {

    override fun functionDataFetcherFactory(target: Any?, kFunction: KFunction<*>) = DataFetcherFactory {
        CustomFunctionDataFetcher(
            target = target,
            fn = kFunction,
            appContext = applicationContext
        )
    }

    override fun propertyDataFetcherFactory(kClass: KClass<*>, kProperty: KProperty<*>): DataFetcherFactory<Any?> =
        if (kProperty.isLateinit) {
            springDataFetcherFactory
        } else {
            super.propertyDataFetcherFactory(kClass, kProperty)
        }
}
