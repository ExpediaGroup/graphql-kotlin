/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.extensions

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * KProperty Extensions:
 * For properties we need to check both the property for annotations and the constructor argument
 */

internal fun KProperty<*>.isPropertyGraphQLIgnored(parentClass: KClass<*>): Boolean = when {
    this.isGraphQLIgnored() -> true
    getConstructorParameter(parentClass)?.isGraphQLIgnored().isTrue() -> true
    this.getter.isGraphQLIgnored() -> true
    else -> false
}

internal fun KProperty<*>.getPropertyDeprecationReason(parentClass: KClass<*>): String? =
    this.getDeprecationReason() ?: getConstructorParameter(parentClass)?.getDeprecationReason()

internal fun KProperty<*>.getPropertyDescription(parentClass: KClass<*>): String? =
    this.getGraphQLDescription() ?: getConstructorParameter(parentClass)?.getGraphQLDescription()

internal fun KProperty<*>.getPropertyName(parentClass: KClass<*>): String? =
    this.getGraphQLName() ?: getConstructorParameter(parentClass)?.getGraphQLName() ?: this.name

internal fun KProperty<*>.getPropertyAnnotations(parentClass: KClass<*>): List<Annotation> =
    this.annotations.union(getConstructorParameter(parentClass)?.annotations.orEmpty()).toList()

private fun KProperty<*>.getConstructorParameter(parentClass: KClass<*>) = parentClass.findConstructorParameter(this.name)
