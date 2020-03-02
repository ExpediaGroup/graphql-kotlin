package com.expediagroup.graphql.plugin.generator

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.Document
import graphql.schema.idl.TypeDefinitionRegistry

data class GraphQLClientGeneratorContext(
    val packageName: String,
    val graphQLSchema: TypeDefinitionRegistry,
    val rootType: String,
    val queryDocument: Document
) {
    val typeNameCache: MutableMap<String, TypeName> = mutableMapOf()
    val typeSpecs: MutableList<TypeSpec> = mutableListOf()
}
