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

package com.expediagroup.graphql.plugin.gradle.config

import java.io.Serializable

/**
 * Holds mapping between custom GraphQL scalar type, corresponding Kotlin type and the converter that will be used to convert to/from
 * raw JSON and Java type.
 *
 * In order to limit the amount of plugin dependencies, we cannot use client-generator GraphQLScalar directly as it is declared as a
 * compileOnly dependency (which will be available on the worker classpath only). We need to re-define the same object here so it will
 * be accessible from build file configurations.
 */
data class GraphQLScalar(
    /** Custom scalar name. */
    val scalar: String,
    /** Fully qualified class name of a custom scalar type, e.g. java.util.UUID */
    val type: String,
    /** Fully qualified class name of a custom converter used to convert to/from raw JSON and [type] */
    val converter: String
) : Serializable
