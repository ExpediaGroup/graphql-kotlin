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

import com.expediagroup.graphql.generator.execution.spi.ArgumentObjectConverter
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class DummyArgumentObjectConverter : ArgumentObjectConverter {
    override val priority: Int = -1

    override fun <T : Any> doesSupport(targetClass: KClass<T>): Boolean = targetClass
        .isSubclassOf(ConvertArgumentValueTest.TestInputCustomConverted::class)

    override fun <T : Any> convert(input: Map<String, *>, targetClass: KClass<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ConvertArgumentValueTest.TestInputCustomConverted(
            input[ConvertArgumentValueTest.TestInputCustomConverted::class.toString()].toString() + ArgumentObjectConverter::class
        ) as T
    }
}
