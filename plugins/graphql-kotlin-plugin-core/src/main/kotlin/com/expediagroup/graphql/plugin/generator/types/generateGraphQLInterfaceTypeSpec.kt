package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.InterfaceTypeDefinition
import graphql.language.SelectionSet

internal fun generateGraphQLInterfaceTypeSpec(context: GraphQLClientGeneratorContext, interfaceDefinition: InterfaceTypeDefinition, selectionSet: SelectionSet?): TypeSpec {
    if (selectionSet == null || selectionSet.selections.isEmpty()) {
        throw RuntimeException("cannot select empty interface")
    }

    val implementations = context.graphQLSchema.getImplementationsOf(interfaceDefinition).map { it.name }
    val interfaceType = generateInterfaceTypeSpec(
        context = context,
        interfaceName = interfaceDefinition.name,
        kdoc = interfaceDefinition.description?.content,
        fields = interfaceDefinition.fieldDefinitions,
        selectionSet = selectionSet,
        implementations = implementations
    )

    context.typeSpecs[interfaceDefinition.name] = interfaceType
    return interfaceType
}
