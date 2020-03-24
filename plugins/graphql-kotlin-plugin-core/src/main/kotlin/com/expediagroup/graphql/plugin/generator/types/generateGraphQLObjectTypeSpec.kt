package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.FragmentDefinition
import graphql.language.FragmentSpread
import graphql.language.ObjectTypeDefinition
import graphql.language.SelectionSet

internal fun generateGraphQLObjectTypeSpec(context: GraphQLClientGeneratorContext, objectDefinition: ObjectTypeDefinition, selectionSet: SelectionSet?, objectNameOverride: String? = null): TypeSpec {
    if (selectionSet == null || selectionSet.selections.isEmpty()) {
        throw RuntimeException("cannot select empty objects")
    }

    val typeName = objectNameOverride ?: objectDefinition.name
    val objectTypeSpecBuilder = TypeSpec.classBuilder(typeName)
    objectTypeSpecBuilder.modifiers.add(KModifier.DATA)
    objectDefinition.description?.content?.let { kdoc ->
        objectTypeSpecBuilder.addKdoc(kdoc)
    }

    val constructorBuilder = FunSpec.constructorBuilder()
    generatePropertySpecs(
        context = context,
        objectName = objectDefinition.name,
        selectionSet = selectionSet,
        fieldDefinitions = objectDefinition.fieldDefinitions
    ).forEach { propertySpec ->
        objectTypeSpecBuilder.addProperty(propertySpec)
        constructorBuilder.addParameter(propertySpec.name, propertySpec.type)
    }

    selectionSet.getSelectionsOfType(FragmentSpread::class.java)
        .forEach { fragment ->
            val fragmentDefinition = context.queryDocument
                .getDefinitionsOfType(FragmentDefinition::class.java)
                .find { it.name == fragment.name } ?: throw RuntimeException("fragment not found")
            generatePropertySpecs(
                context = context,
                objectName = objectDefinition.name,
                selectionSet = fragmentDefinition.selectionSet,
                fieldDefinitions = objectDefinition.fieldDefinitions
            ).forEach { propertySpec ->
                objectTypeSpecBuilder.addProperty(propertySpec)
                constructorBuilder.addParameter(propertySpec.name, propertySpec.type)
            }
        }
    objectTypeSpecBuilder.primaryConstructor(constructorBuilder.build())

    val objectTypeSpec = objectTypeSpecBuilder.build()
    context.typeSpecs[typeName] = objectTypeSpec
    return objectTypeSpec
}
