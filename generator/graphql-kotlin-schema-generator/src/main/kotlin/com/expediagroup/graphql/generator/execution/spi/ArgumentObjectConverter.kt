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

package com.expediagroup.graphql.generator.execution.spi

import kotlin.reflect.KClass

/**
 * [ArgumentObjectConverter] allows application and library developers to provide their own implementation
 * and logic for how to convert the input argument. The [ArgumentObjectConverter] provide a way to extend
 * this library easily. It is using the service provider spec from [java.util.ServiceLoader].
 *
 * In order to use custom implementation(s) of [ArgumentObjectConverter]
 * 1) you need to implement this interface, and
 * 2) you need to register some/all of your custom [ArgumentObjectConverter] in the services file.
 *
 * All the registered implementations of [ArgumentObjectConverter] are sorted by
 * the return value of [ArgumentObjectConverter.priority] in ascending order. Default implementation
 * is provided by [DefaultArgumentObjectConverter] with [DefaultArgumentObjectConverter.priority] == [Int.MAX_VALUE].
 *
 */
interface ArgumentObjectConverter {

    /**
     * The return value is used to sort all registered [ArgumentObjectConverter] in ascending order.
     */
    val priority: Int

    /**
     * Returns true if this instance of [ArgumentObjectConverter] can convert the input argument to
     * the provided target class [targetClass], otherwise this method returns false.
     */
    fun <T : Any> doesSupport(targetClass: KClass<T>): Boolean

    /**
     * Converts the [input] to an instance of [targetClass] and
     * returns an instance of [targetClass].
     */
    fun <T : Any> convert(input: Map<String, *>, targetClass: KClass<T>): T
}
