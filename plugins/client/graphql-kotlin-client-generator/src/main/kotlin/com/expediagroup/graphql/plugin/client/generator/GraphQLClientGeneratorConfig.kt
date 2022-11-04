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

package com.expediagroup.graphql.plugin.client.generator

import com.squareup.kotlinpoet.ClassName
import graphql.parser.ParserOptions

/**
 * GraphQL client generator configuration.
 */
data class GraphQLClientGeneratorConfig(
    /** Target package for the generated classes. **/
    val packageName: String,
    /** Boolean flag indicating whether to allow selection of deprecated fields. Defaults to false. */
    val allowDeprecated: Boolean = false,
    /** Custom scalar type to converter mapping. */
    val customScalarMap: Map<String, GraphQLScalar> = emptyMap(),
    /** Type of JSON serializer to be used. */
    val serializer: GraphQLSerializer = GraphQLSerializer.JACKSON,
    /** Explicit opt-in flag to enable support for optional inputs. */
    val useOptionalInputWrapper: Boolean = false,
    /** Set parser options for processing GraphQL queries and schema definition language documents */
    val parserOptions: ParserOptions.Builder.() -> Unit = {}
)

/**
 * Custom scalar to converter mapping.
 */
data class GraphQLScalar(
    /** Custom GraphQL scalar name. */
    val scalar: String,
    /** Fully qualified class name of a custom scalar type, e.g. java.util.UUID */
    val type: String,
    /** Fully qualified class name of a custom converter used to convert to/from raw JSON and [type] */
    val converter: String
) {
    val className: ClassName by lazy { type.toClassName() }
    val converterClassName: ClassName by lazy { converter.toClassName() }

    private fun String.toClassName(): ClassName {
        val index = this.lastIndexOf('.')
        return if (index < 0) {
            ClassName("", this)
        } else {
            ClassName(this.substring(0, index), this.substring(index + 1))
        }
    }
}

/**
 * Type of JSON serializer that will be used to generate the data classes.
 */
enum class GraphQLSerializer {
    KOTLINX,
    JACKSON
}
