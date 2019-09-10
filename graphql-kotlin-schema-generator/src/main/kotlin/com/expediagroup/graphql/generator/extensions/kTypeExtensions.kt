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

package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.exceptions.InvalidListTypeException
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.jvmErasure

private val primitiveArrayTypes = mapOf(
    IntArray::class to Int::class,
    LongArray::class to Long::class,
    ShortArray::class to Short::class,
    FloatArray::class to Float::class,
    DoubleArray::class to Double::class,
    CharArray::class to Char::class,
    BooleanArray::class to Boolean::class
)

internal fun KType.getKClass() = this.jvmErasure

@Throws(InvalidListTypeException::class)
internal fun KType.getTypeOfFirstArgument(): KType =
    this.arguments.firstOrNull()?.type ?: throw InvalidListTypeException(this)

internal fun KType.getWrappedType(): KType {
    val primitiveClass = primitiveArrayTypes[this.getKClass()]
    return when {
        primitiveClass != null -> primitiveClass.createType()
        else -> this.getTypeOfFirstArgument()
    }
}

internal fun KType.getSimpleName(isInputType: Boolean = false): String = this.getKClass().getSimpleName(isInputType)

internal val KType.qualifiedName: String
    get() = this.getKClass().getQualifiedName()
