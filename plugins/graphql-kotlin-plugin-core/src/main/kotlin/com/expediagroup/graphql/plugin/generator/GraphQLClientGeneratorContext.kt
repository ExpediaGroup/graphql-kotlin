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

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.Document
import graphql.schema.idl.TypeDefinitionRegistry

/**
 * GraphQL client generator context.
 *
 * Context is created per each processed GraphQL query and contains configuration information, schema and query information as well as holds caches of all generated types, aliases and class names.
 */
data class GraphQLClientGeneratorContext(
    val packageName: String,
    val graphQLSchema: TypeDefinitionRegistry,
    val rootType: String,
    val queryDocument: Document,
    val allowDeprecated: Boolean = false,
    val scalarTypeToConverterMapping: Map<String, ScalarConverterMapping> = emptyMap()
) {
    val classNameCache: MutableMap<String, MutableList<ClassName>> = mutableMapOf()
    val typeSpecs: MutableMap<String, TypeSpec> = mutableMapOf()
    val typeAliases: MutableMap<String, TypeAliasSpec> = mutableMapOf()

    val typeToSelectionSetMap: MutableMap<String, Set<String>> = mutableMapOf()
}
