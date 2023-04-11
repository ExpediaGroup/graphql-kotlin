/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.plugin.graalvm

/**
 * GraalVM reflect metadata for classes.
 */
data class ClassMetadata(
    val name: String,
    val allDeclaredConstructors: Boolean? = null,
    val allDeclaredFields: Boolean? = null,
    val allDeclaredMethods: Boolean? = null,
    val allPublicConstructors: Boolean? = null,
    val allPublicMethods: Boolean? = null,
    val queryAllDeclaredMethods: Boolean? = null,
    val queryAllDeclaredConstructors: Boolean? = null,
    val fields: List<FieldMetadata>? = null,
    val methods: List<MethodMetadata>? = null
)

internal data class MutableClassMetadata(
    val name: String,
    var allDeclaredConstructors: Boolean? = null,
    var allDeclaredFields: Boolean? = null,
    var allDeclaredMethods: Boolean? = null,
    var allPublicConstructors: Boolean? = null,
    var allPublicMethods: Boolean? = null,
    var queryAllDeclaredMethods: Boolean? = null,
    var queryAllDeclaredConstructors: Boolean? = null,
    val fields: List<FieldMetadata>? = null,
    val methods: MutableList<MethodMetadata>? = null
)

internal fun MutableClassMetadata.toClassMetadata() = ClassMetadata(
    name,
    allDeclaredConstructors,
    allDeclaredFields,
    allDeclaredMethods,
    allPublicConstructors,
    allPublicMethods,
    queryAllDeclaredMethods,
    queryAllDeclaredConstructors,
    fields,
    methods?.sortedBy { method -> method.name }
)

/**
 * GraalVM reflect metadata for class fields.
 */
data class FieldMetadata(val name: String)

/**
 * GraalVM reflect metadata for class methods.
 */
data class MethodMetadata(val name: String, val parameterTypes: List<String> = emptyList())
