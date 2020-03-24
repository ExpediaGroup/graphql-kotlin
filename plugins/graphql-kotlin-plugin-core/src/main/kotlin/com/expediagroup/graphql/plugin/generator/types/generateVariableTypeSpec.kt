package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.VariableDefinition

internal fun generateVariableTypeSpec(context: GraphQLClientGeneratorContext, variableDefinitions: List<VariableDefinition>): TypeSpec? {
    val variableTypeSpec = TypeSpec.classBuilder("Variables")
        .addModifiers(KModifier.DATA)

    val constructorSpec = FunSpec.constructorBuilder()
    variableDefinitions.forEach { variableDef ->
        val kotlinTypeName = generateTypeName(context, variableDef.type)

        constructorSpec.addParameter(variableDef.name, kotlinTypeName)
        variableTypeSpec.addProperty(PropertySpec.builder(variableDef.name, kotlinTypeName)
            .initializer(variableDef.name).build())
    }

    variableTypeSpec.primaryConstructor(constructorSpec.build())
    return if (variableTypeSpec.propertySpecs.isEmpty()) {
        null
    } else {
        variableTypeSpec.build()
    }
}
