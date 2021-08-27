/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.types.utils

import com.expediagroup.graphql.generator.exceptions.InvalidGraphQLEnumValueException
import com.expediagroup.graphql.generator.exceptions.InvalidGraphQLNameException
import kotlin.reflect.KClass

internal val validGraphQLNameRegex = Regex("[_A-Za-z][_0-9A-Za-z]*")

/**
 * Throws an exception if passed in name is not a valid GraphQL name.
 */
internal fun validateGraphQLName(name: String, kClass: KClass<*>) {
    if (!name.matches(validGraphQLNameRegex)) {
        throw InvalidGraphQLNameException(kClass, name)
    }
}

/**
 * Throws an exception if passed in enum value is not a valid GraphQL name.
 */
internal fun validateGraphQLEnumValue(enumValue: String, enumKClass: KClass<*>) {
    if (!enumValue.matches(validGraphQLNameRegex)) {
        throw InvalidGraphQLEnumValueException(enumKClass, enumValue)
    }
}
