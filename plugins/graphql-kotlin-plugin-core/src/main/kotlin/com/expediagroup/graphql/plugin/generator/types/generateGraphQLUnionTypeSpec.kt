package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.ObjectTypeDefinition
import graphql.language.SelectionSet
import graphql.language.UnionTypeDefinition

internal fun generateGraphQLUnionTypeSpec(context: GraphQLClientGeneratorContext, unionDefinition: UnionTypeDefinition, selectionSet: SelectionSet?): TypeSpec {
    if (selectionSet == null || selectionSet.selections.isEmpty()) {
        throw RuntimeException("cannot select empty union")
    }

    // unsure why union member types are just types even though GraphQL spec currently only supports objects, applying filter simply ensures we have a list of objects
    val unionImplementations = unionDefinition.memberTypes.filterIsInstance(ObjectTypeDefinition::class.java).map { it.name }
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
