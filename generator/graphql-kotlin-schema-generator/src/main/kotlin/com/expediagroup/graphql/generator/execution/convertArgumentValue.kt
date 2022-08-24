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

import com.expediagroup.graphql.generator.exceptions.MultipleConstructorsFound
import com.expediagroup.graphql.generator.exceptions.PrimaryConstructorNotFound
import com.expediagroup.graphql.generator.internal.extensions.getGraphQLName
import com.expediagroup.graphql.generator.internal.extensions.getKClass
import com.expediagroup.graphql.generator.internal.extensions.getName
import com.expediagroup.graphql.generator.internal.extensions.getTypeOfFirstArgument
import com.expediagroup.graphql.generator.internal.extensions.isNotOptionalNullable
import com.expediagroup.graphql.generator.internal.extensions.isOptionalInputType
import com.expediagroup.graphql.generator.internal.extensions.isSubclassOf
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

/**
 * Convert the argument from the argument map to a class we can pass to the Kotlin function.
 */
internal fun convertArgumentValue(
    argumentName: String,
    param: KParameter,
    argumentMap: Map<String, Any?>
): Any? {
    val argumentValue = argumentMap[argumentName]
    return when {
        param.type.isOptionalInputType() -> {
            when {
                !argumentMap.containsKey(argumentName) -> OptionalInput.Undefined
                argumentValue == null -> OptionalInput.Defined(null)
                else -> {
                    val paramType = param.type.getTypeOfFirstArgument()
                    val value = convertValue(paramType, argumentValue)
                    OptionalInput.Defined(value)
                }
            }
        }
        else -> convertValue(param.type, argumentValue)
    }
}

/**
 * The value may already be parsed, so we can perform some checks to avoid duplicate deserialization
 */
private fun convertValue(
    paramType: KType,
    argumentValue: Any?
): Any? {
    // The input given is a list, iterate over each value to return a parsed list
    if (argumentValue is Iterable<*>) {
        return argumentValue.map {
            val wrappedType = paramType.getTypeOfFirstArgument()
            convertValue(wrappedType, it)
        }
    }

    // If the value is a generic map, parse each entry which may have some values already parsed
    if (argumentValue is Map<*, *>) {
        @Suppress("UNCHECKED_CAST")
        return mapToKotlinObject(argumentValue as Map<String, *>, paramType.getKClass())
    }

    // If the value is enum we need to find the correct value
    if (argumentValue is String && paramType.isSubclassOf(Enum::class)) {
        return mapToEnumValue(paramType, argumentValue)
    }

    // If param type is inline value class we need to wrap the value
    if (argumentValue != null && paramType.getKClass().isValue) {
        return mapToInlineValueClass(argumentValue, paramType.getKClass())
    }

    // Value is already parsed so we can return it as-is
    return argumentValue
}

/**
 * At this point all custom scalars have been converted by graphql-java so
 * the only thing left to parse is object maps into the nested Kotlin classes
 */
private fun <T : Any> mapToKotlinObject(input: Map<String, *>, targetClass: KClass<T>): T {

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

private fun mapToEnumValue(paramType: KType, enumValue: String): Enum<*> = paramType.getKClass()
    .java
    .enumConstants
    .filterIsInstance(Enum::class.java)
    .map { enum ->
        val enumValueField = paramType.getKClass().java.getField(enum.name)
        enumValueField.getGraphQLName() to enum
    }
    .first { (name, _) -> name == enumValue }
    .second

private fun <T : Any> mapToInlineValueClass(value: Any?, targetClass: KClass<T>): T {
    val targetConstructor = targetClass.primaryConstructor ?: throw PrimaryConstructorNotFound(targetClass)
    // if the user has registered a coercer for the value class, it may already be an instance of the target type
    @Suppress("UNCHECKED_CAST")
    return if (targetClass.isInstance(value)) value as T else targetConstructor.call(value)
}
