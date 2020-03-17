package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.Field
import graphql.language.FieldDefinition
import graphql.language.FragmentDefinition
import graphql.language.FragmentSpread
import graphql.language.NonNullType
import graphql.language.ObjectTypeDefinition
import graphql.language.SelectionSet

internal fun generateObjectTypeSpec(context: GraphQLClientGeneratorContext, objectDefinition: ObjectTypeDefinition, selectionSet: SelectionSet, objectNameOverride: String? = null): TypeSpec {
    // TODO what if we select same object but different fields -> need to use different key -> name + selection set + fragment
    val typeName = objectNameOverride ?: objectDefinition.name
    val objectTypeSpecBuilder = TypeSpec.classBuilder(typeName)
    objectTypeSpecBuilder.modifiers.add(KModifier.DATA)
    objectDefinition.description?.content?.let { kdoc ->
        objectTypeSpecBuilder.addKdoc(kdoc)
    }

    val constructorBuilder = FunSpec.constructorBuilder()
    selectionSet.generatePropertySpecs(
        context,
        objectDefinition.fieldDefinitions,
        objectDefinition.name
    ).forEach { propertySpec ->
        objectTypeSpecBuilder.addProperty(propertySpec)
        constructorBuilder.addParameter(propertySpec.name, propertySpec.type)
    }

    selectionSet.getSelectionsOfType(FragmentSpread::class.java)
        .forEach { fragment ->
            val fragmentDefinition = context.queryDocument
                .getDefinitionsOfType(FragmentDefinition::class.java)
                .find { it.name == fragment.name } ?: throw RuntimeException("fragment not found")
            fragmentDefinition.selectionSet.generatePropertySpecs(
                context,
                objectDefinition.fieldDefinitions,
                objectDefinition.name
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

internal fun SelectionSet.generatePropertySpecs(
    context: GraphQLClientGeneratorContext,
    fieldDefinitions: List<FieldDefinition>,
    objectName: String
): List<PropertySpec> = this.getSelectionsOfType(Field::class.java)
    .filterNot { it.name == "__typename" }
    .map { selectedField ->
        val fieldDefinition = fieldDefinitions.find { it.name == selectedField.name }
            ?: throw RuntimeException("unable to find corresponding field definition ${selectedField.name} in $objectName")
        val nullable = fieldDefinition.type !is NonNullType
        val kotlinFieldType = generateKotlinTypeName(context, fieldDefinition.type, selectedField.selectionSet)
        val fieldName = selectedField.alias ?: fieldDefinition.name

        val propertySpecBuilder = PropertySpec.builder(fieldName, kotlinFieldType.copy(nullable = nullable))
            .initializer(fieldName)
        fieldDefinition.description?.content?.let { kdoc ->
            propertySpecBuilder.addKdoc(kdoc)
        }
        propertySpecBuilder.build()
    }
