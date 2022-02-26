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

package com.expediagroup.graphql.generator.internal.extensions

import com.expediagroup.graphql.generator.exceptions.InvalidWrappedTypeException
import com.expediagroup.graphql.generator.execution.OptionalInput
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

internal fun KType.getKClass() = this.jvmErasure

internal fun KType.getJavaClass(): Class<*> = this.getKClass().java

internal fun KType.isSubclassOf(kClass: KClass<*>) = this.getKClass().isSubclassOf(kClass)

internal fun KType.isList() = this.isSubclassOf(List::class)

internal fun KType.isArray() = this.getJavaClass().isArray

internal fun KType.isListType() = this.isList() || this.isArray()

internal fun KType.isOptionalInputType() = this.isSubclassOf(OptionalInput::class)

internal fun KType.unwrapOptionalInputType() = if (this.isOptionalInputType()) {
    this.getTypeOfFirstArgument().withNullability(true)
} else {
    this
}

@Throws(InvalidWrappedTypeException::class)
internal fun KType.getTypeOfFirstArgument(): KType =
    this.arguments.firstOrNull()?.type ?: throw InvalidWrappedTypeException(this)

internal fun KType.getSimpleName(isInputType: Boolean = false): String = this.getKClass().getSimpleName(isInputType)

internal val KType.qualifiedName: String
    get() = this.getKClass().getQualifiedName()
