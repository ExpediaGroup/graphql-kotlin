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

package com.expediagroup.graphql.generator.execution

import graphql.schema.DataFetcherFactory
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

/**
 * DataFetcherFactoryProvider is used during schema construction to obtain [DataFetcherFactory] that should be used
 * for target function and property resolution.
 */
interface KotlinDataFetcherFactoryProvider {

    /**
     * Retrieve an instance of [DataFetcherFactory] that will be used to resolve target function.
     *
     * @param target target object that performs the data fetching or NULL if target object should be dynamically
     * retrieved during data fetcher execution from [graphql.schema.DataFetchingEnvironment]
     * @param kFunction Kotlin function being invoked
     */
    fun functionDataFetcherFactory(target: Any?, kFunction: KFunction<*>): DataFetcherFactory<Any?>

    /**
     * Retrieve an instance of [DataFetcherFactory] that will be used to resolve target property.
     *
     * @param kClass parent class that contains this property
     * @param kProperty Kotlin property that should be resolved
     */
    fun propertyDataFetcherFactory(kClass: KClass<*>, kProperty: KProperty<*>): DataFetcherFactory<Any?>
}

/**
 * SimpleKotlinDataFetcherFactoryProvider is the default data fetcher factory provider that is used during schema construction
 * to obtain [DataFetcherFactory] that should be used for target function and property resolution.
 */
open class SimpleKotlinDataFetcherFactoryProvider : KotlinDataFetcherFactoryProvider {

    override fun functionDataFetcherFactory(target: Any?, kFunction: KFunction<*>) = DataFetcherFactory {
        FunctionDataFetcher(
            target = target,
            fn = kFunction
        )
    }

    override fun propertyDataFetcherFactory(kClass: KClass<*>, kProperty: KProperty<*>) = DataFetcherFactory {
        PropertyDataFetcher(kProperty.getter)
    }
}
