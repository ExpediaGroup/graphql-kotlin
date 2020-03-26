package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.squareup.kotlinpoet.TypeAliasSpec
import graphql.language.ScalarTypeDefinition

internal fun generateGraphQLCustomScalarTypeAlias(context: GraphQLClientGeneratorContext, scalarTypeDefinition: ScalarTypeDefinition): TypeAliasSpec {
    val typeAliasSpec = TypeAliasSpec.builder(scalarTypeDefinition.name, String::class)
    scalarTypeDefinition.description?.content?.let { kdoc ->
        typeAliasSpec.addKdoc(kdoc)
    }

    val typeAlias = typeAliasSpec.build()
    context.typeAliases[scalarTypeDefinition.name] = typeAlias
    return typeAlias
}
