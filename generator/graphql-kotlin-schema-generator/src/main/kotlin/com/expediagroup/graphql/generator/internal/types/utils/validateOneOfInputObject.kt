/*
 * Copyright 2026 Expedia, Inc
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

import com.expediagroup.graphql.generator.exceptions.DuplicateOneOfInputFieldException
import com.expediagroup.graphql.generator.exceptions.InvalidGraphQLOneOfUnwrappedFieldException
import com.expediagroup.graphql.generator.exceptions.InvalidGraphQLOneOfTargetException
import com.expediagroup.graphql.generator.exceptions.NoGraphQLOneOfImplementationsException
import com.expediagroup.graphql.generator.internal.extensions.isPublic
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

internal fun validateOneOfInputObjectSealedInterface(kClass: KClass<*>) {
    if (!kClass.java.isInterface || !kClass.isSealed) {
        throw InvalidGraphQLOneOfTargetException(kClass)
    }
    if (kClass.sealedSubclasses.isEmpty()) {
        throw NoGraphQLOneOfImplementationsException(kClass)
    }
}

internal fun validateOneOfInputDuplicatedFieldNames(parent: KClass<*>, fieldNames: List<String>) {
    val duplicateNames = fieldNames
        .groupingBy { it }
        .eachCount()
        .filterValues { it > 1 }
        .keys
    if (duplicateNames.isNotEmpty()) {
        throw DuplicateOneOfInputFieldException(parent, duplicateNames)
    }
}

internal fun getValidOneOfUnwrappedFieldParameter(kClass: KClass<*>): KParameter {
    val primaryConstructor = kClass.primaryConstructor
        ?: throw InvalidGraphQLOneOfUnwrappedFieldException(kClass)

    if (
        kClass.java.isInterface ||
        kClass.isAbstract ||
        kClass.isSealed ||
        !primaryConstructor.isPublic() ||
        primaryConstructor.parameters.size != 1
    ) {
        throw InvalidGraphQLOneOfUnwrappedFieldException(kClass)
    }

    return primaryConstructor.parameters.single()
}
