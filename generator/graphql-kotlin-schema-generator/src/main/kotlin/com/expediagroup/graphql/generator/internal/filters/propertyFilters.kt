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

package com.expediagroup.graphql.generator.internal.filters

import com.expediagroup.graphql.generator.exceptions.InvalidPropertyReturnTypeException
import com.expediagroup.graphql.generator.internal.extensions.isGraphQLIgnored
import com.expediagroup.graphql.generator.internal.extensions.isPropertyGraphQLIgnored
import com.expediagroup.graphql.generator.internal.extensions.isPublic
import com.expediagroup.graphql.generator.internal.extensions.qualifiedName
import com.expediagroup.graphql.generator.internal.types.utils.validGraphQLNameRegex
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

private typealias PropertyFilter = (KProperty<*>, KClass<*>) -> Boolean

private val blockedTypes: List<String> = listOf("kotlin.reflect.KClass")

private val isPropertyPublic: PropertyFilter = { prop, _ -> prop.isPublic() }
private val isPropertyNotGraphQLIgnored: PropertyFilter = { prop, parentClass -> prop.isPropertyGraphQLIgnored(parentClass).not() }
private val isNotBlockedType: PropertyFilter = { prop, _ -> blockedTypes.contains(prop.returnType.qualifiedName).not() }
private val isValidPropertyName: PropertyFilter = { prop, _ -> prop.name.matches(validGraphQLNameRegex) }
@OptIn(ExperimentalStdlibApi::class)
private val isNotLambda: PropertyFilter = { prop, parentClass ->
    if (prop.returnType.isSubtypeOf(typeOf<Function<*>>())) {
        throw InvalidPropertyReturnTypeException(parentClass, prop)
    } else {
        true
    }
}

private val isNotIgnoredFromSuperClass: PropertyFilter = { prop, parentClass ->
    val superPropsIgnored = parentClass.supertypes
        .flatMap { superType ->
            superType.jvmErasure.memberProperties
                .filter { it.isGraphQLIgnored() }
                .filter { superProp -> basicPropertyFilters.all { it.invoke(superProp, superType::class) } }
        }

    superPropsIgnored.none { superProp ->
        superProp.name == prop.name &&
            superProp.returnType == prop.returnType
    }
}

private val basicPropertyFilters = listOf(isPropertyPublic, isNotLambda, isNotBlockedType)
internal val propertyFilters: List<PropertyFilter> = listOf(isPropertyNotGraphQLIgnored) + basicPropertyFilters + isNotIgnoredFromSuperClass + isValidPropertyName
