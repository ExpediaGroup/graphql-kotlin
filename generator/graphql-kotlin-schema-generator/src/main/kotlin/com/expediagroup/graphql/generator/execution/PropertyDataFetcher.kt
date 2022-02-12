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

import graphql.TrivialDataFetcher
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlin.reflect.KProperty

/**
 * Property [DataFetcher] that directly invokes underlying property getter.
 *
 * @param propertyGetter Kotlin property getter that will be invoked to resolve a field
 */
class PropertyDataFetcher(private val propertyGetter: KProperty.Getter<*>) : TrivialDataFetcher<Any?> {

    /**
     * Invokes target getter function.
     */
    override fun get(environment: DataFetchingEnvironment): Any? = environment.getSource<Any?>()?.let { instance ->
        propertyGetter.call(instance)
    }
}
