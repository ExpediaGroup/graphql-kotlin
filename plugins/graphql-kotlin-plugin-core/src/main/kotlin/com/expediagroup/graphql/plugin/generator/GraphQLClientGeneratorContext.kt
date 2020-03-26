package com.expediagroup.graphql.plugin.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.Document
import graphql.schema.idl.TypeDefinitionRegistry

data class GraphQLClientGeneratorContext(
    val packageName: String,
    val graphQLSchema: TypeDefinitionRegistry,
    val rootType: String,
    val queryDocument: Document,
    val allowDeprecated: Boolean = false,
    val scalarTypeToConverterMapping: Map<String, CustomScalarConverterMapping> = emptyMap()
) {
    val classNameCache: MutableMap<String, ClassName> = mutableMapOf()
    val typeSpecs: MutableMap<String, TypeSpec> = mutableMapOf()
}
