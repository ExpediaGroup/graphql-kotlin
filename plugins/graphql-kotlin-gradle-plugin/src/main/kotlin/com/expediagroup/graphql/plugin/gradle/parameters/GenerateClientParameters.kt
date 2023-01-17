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

package com.expediagroup.graphql.plugin.gradle.parameters

import com.expediagroup.graphql.plugin.gradle.config.GraphQLParserOptions
import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters
import java.io.File

/**
 * WorkParameters used for generating GraphQL HTTP client.
 */
interface GenerateClientParameters : WorkParameters {
    /** Target package for the generated classes. **/
    val packageName: Property<String>
    /** Boolean flag indicating whether to allow selection of deprecated fields. */
    val allowDeprecated: Property<Boolean>
    /** Optional list of custom scalars and their mappings to Kotlin types and a target converter. */
    val customScalars: ListProperty<GraphQLScalar>
    /** Type of JSON serializer that will be used to generate the data classes. */
    val serializer: Property<GraphQLSerializer>
    /** GraphQL schema file path that will be used to generate client code. */
    val schemaPath: Property<String>
    /** List of query files that will be processed to generate HTTP clients. */
    val queryFiles: ListProperty<File>
    /** Directory where to save the generated source files. */
    val targetDirectory: Property<File>
    /** Explicit opt-in flag to wrap nullable arguments in OptionalInput that supports both null and undefined values. */
    val useOptionalInputWrapper: Property<Boolean>
    /** Set parser options for processing GraphQL queries and schema definition language documents */
    val parserOptions: Property<GraphQLParserOptions>
}
