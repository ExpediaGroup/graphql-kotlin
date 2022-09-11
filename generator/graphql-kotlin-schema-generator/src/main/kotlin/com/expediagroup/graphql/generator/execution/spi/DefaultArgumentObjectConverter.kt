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

import com.expediagroup.graphql.generator.exceptions.MultipleConstructorsFound
import com.expediagroup.graphql.generator.exceptions.PrimaryConstructorNotFound
import com.expediagroup.graphql.generator.execution.convertArgumentValue
import com.expediagroup.graphql.generator.internal.extensions.getName
import com.expediagroup.graphql.generator.internal.extensions.isNotOptionalNullable
import com.expediagroup.graphql.generator.internal.extensions.isOptionalInputType
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Default implementation of [ArgumentObjectConverter] as fallback.
 * The [DefaultArgumentObjectConverter.priority] of this convert is always [Int.MAX_VALUE].
 */
class DefaultArgumentObjectConverter : ArgumentObjectConverter {

    /**
     * It is always [Int.MAX_VALUE].
     */
    override val priority: Int = Int.MAX_VALUE

    /**
     * It supports all classes, to it always returns true.
     */
    override fun <T : Any> doesSupport(targetClass: KClass<T>): Boolean = true

    /**
     * Default and Fallback implementation to convert [input] to an instance of [targetClass].
     */
    override fun <T : Any> convert(input: Map<String, *>, targetClass: KClass<T>): T {
        val targetConstructor = targetClass.primaryConstructor ?: run {
            if (targetClass.constructors.size == 1) {
                targetClass.constructors.first()
            } else if (targetClass.constructors.size > 1) {
                throw MultipleConstructorsFound(targetClass)
            } else {
                throw PrimaryConstructorNotFound(targetClass)
            }
        }

        // filter parameters that are actually in the input in order to rely on parameters default values
        // in target constructor
        val constructorParameters = targetConstructor.parameters.filter { parameter ->
            input.containsKey(parameter.getName()) ||
                parameter.type.isOptionalInputType() ||

                // for nullable parameters that have no explicit default, we pass in null if not in input
                parameter.isNotOptionalNullable()
        }
        val constructorArguments = constructorParameters.associateWith { parameter ->
            convertArgumentValue(parameter.getName(), parameter, input)
        }
        return targetConstructor.callBy(constructorArguments)
    }
}
