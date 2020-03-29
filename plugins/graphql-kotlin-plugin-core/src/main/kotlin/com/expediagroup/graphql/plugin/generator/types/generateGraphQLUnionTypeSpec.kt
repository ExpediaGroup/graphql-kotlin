package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.SelectionSet
import graphql.language.TypeName
import graphql.language.UnionTypeDefinition

internal fun generateGraphQLUnionTypeSpec(context: GraphQLClientGeneratorContext, unionDefinition: UnionTypeDefinition, selectionSet: SelectionSet?): TypeSpec {
    if (selectionSet == null || selectionSet.selections.isEmpty()) {
        throw RuntimeException("cannot select empty union")
    }

    val unionImplementations = unionDefinition.memberTypes.filterIsInstance(TypeName::class.java).map { it.name }
    val unionType = generateInterfaceTypeSpec(
        context = context,
        interfaceName = unionDefinition.name,
        kdoc = unionDefinition.description?.content,
        fields = null,
        selectionSet = selectionSet,
        implementations = unionImplementations
    )

    context.typeSpecs[unionDefinition.name] = unionType
    return unionType
}
