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

package com.expediagroup.graphql.generator.execution

import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactory
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.LightDataFetcher
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

/**
 * Singleton Property [DataFetcher] that stores references to underlying properties getters.
 */
internal object SingletonPropertyDataFetcher : LightDataFetcher<Any?> {

    private val logger = LoggerFactory.getLogger(SingletonPropertyDataFetcher::class.java)
    val factory: DataFetcherFactory<Any?> = DataFetcherFactory<Any?> { SingletonPropertyDataFetcher }
    private val getters: ConcurrentHashMap<String, KProperty.Getter<*>> = ConcurrentHashMap()

    fun register(kClass: KClass<*>, kProperty: KProperty<*>) {
        getters.computeIfAbsent("${kClass.java.name}.${kProperty.name}") {
            kProperty.getter
        }
    }

    override fun get(
        fieldDefinition: GraphQLFieldDefinition,
        sourceObject: Any?,
        environmentSupplier: Supplier<DataFetchingEnvironment>
    ): Any? {
        if (sourceObject == null) return null

        val getter = getters["${sourceObject.javaClass.name}.${fieldDefinition.name}"]
        if (getter != null) return getter.call(sourceObject)

        return sourceObject::class.memberProperties
            .find { it.name == fieldDefinition.name }
            ?.let { kProperty ->
                register(sourceObject::class, kProperty)
                return kProperty.getter.call(sourceObject)
            } ?: run {
            logger.error("getter method not found: ${sourceObject.javaClass.name}.${fieldDefinition.name}")
            return null
        }
    }

    override fun get(environment: DataFetchingEnvironment): Any? =
        get(environment.fieldDefinition, environment.getSource()) { environment }
}
