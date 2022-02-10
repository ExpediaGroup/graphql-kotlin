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

package com.expediagroup.graphql.plugin.client.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeName
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
    val operationName: String,
    val queryDocument: Document,
    val allowDeprecated: Boolean = false,
    val customScalarMap: Map<String, GraphQLScalar> = mapOf(),
    val serializer: GraphQLSerializer = GraphQLSerializer.JACKSON,
    val useOptionalInputWrapper: Boolean = false
) {
    // per operation caches
    val typeSpecs: MutableMap<ClassName, TypeSpec> = mutableMapOf()
    val polymorphicTypes: MutableMap<ClassName, MutableList<ClassName>> = mutableMapOf()

    // shared type caches
    val enumClassToTypeSpecs: MutableMap<ClassName, TypeSpec> = mutableMapOf()
    val inputClassToTypeSpecs: MutableMap<ClassName, TypeSpec> = mutableMapOf()
    val scalarClassToConverterTypeSpecs: MutableMap<ClassName, ScalarConverterInfo> = mutableMapOf()
    val typeAliases: MutableMap<String, TypeAliasSpec> = mutableMapOf()
    internal fun isTypeAlias(typeName: String) = typeAliases.containsKey(typeName)

    // class name and type selection caches
    val classNameCache: MutableMap<String, MutableList<ClassName>> = mutableMapOf()
    val typeToSelectionSetMap: MutableMap<String, Set<String>> = mutableMapOf()

    private val customScalarClassNames: Set<ClassName> = customScalarMap.values.map { it.className }.toSet()
    internal fun isCustomScalar(typeName: TypeName): Boolean = customScalarClassNames.contains(typeName)
    var requireOptionalSerializer = false
    val optionalSerializers: MutableMap<ClassName, TypeSpec> = mutableMapOf()
}

sealed class ScalarConverterInfo {
    data class JacksonConvertersInfo(
        val serializerClassName: ClassName,
        val serializerTypeSpec: TypeSpec,
        val deserializerClassName: ClassName,
        val deserializerTypeSpec: TypeSpec
    ) : ScalarConverterInfo()

    data class KotlinxSerializerInfo(
        val serializerClassName: ClassName,
        val serializerTypeSpec: TypeSpec
    ) : ScalarConverterInfo()
}
