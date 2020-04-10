/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.plugin.generator

import java.io.Serializable

/**
 * GraphQL client generator configuration.
 */
data class GraphQLClientGeneratorConfig(
    /** Target package for the generated classes. **/
    val packageName: String,
    /** Boolean flag indicating whether to allow selection of deprecated fields. Defaults to false. */
    val allowDeprecated: Boolean = false,
    /** Custom scalar type to converter mapping. */
    val scalarTypeToConverterMapping: Map<String, ScalarConverterMapping> = emptyMap()
)

/**
 * Custom scalar to converter mapping.
 */
data class ScalarConverterMapping(
    /** Fully qualified class name of a custom scalar type, e.g. java.util.UUID */
    val type: String,
    /** Fully qualified class name of a custom converter used to convert to/from raw JSON and [type] */
    val converter: String
) : Serializable
