/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.plugin.client.generator.types

import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGeneratorContext
import com.expediagroup.graphql.plugin.client.generator.GraphQLSerializer
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.InputObjectTypeDefinition
import kotlinx.serialization.Serializable

/**
 * Generate [TypeSpec] data class from the specified input object definition where are fields are mapped to corresponding Kotlin property.
 */
internal fun generateGraphQLInputObjectTypeSpec(context: GraphQLClientGeneratorContext, inputObjectDefinition: InputObjectTypeDefinition): TypeSpec {
    val inputObjectTypeSpecBuilder = TypeSpec.classBuilder(inputObjectDefinition.name)
        .addModifiers(KModifier.DATA)
    inputObjectDefinition.description?.content?.let { kdoc ->
        inputObjectTypeSpecBuilder.addKdoc("%L", kdoc)
    }

    if (context.serializer == GraphQLSerializer.KOTLINX) {
        inputObjectTypeSpecBuilder.addAnnotation(Serializable::class)
    }

    val constructorBuilder = FunSpec.constructorBuilder()
    inputObjectDefinition.inputValueDefinitions.forEach { fieldDefinition ->
        val kotlinFieldType = generateTypeName(context, fieldDefinition.type)
        val fieldName = fieldDefinition.name

        val inputFieldType = if (context.serializer == GraphQLSerializer.JACKSON && kotlinFieldType.isNullable) {
            ClassName("com.expediagroup.graphql.client.jackson.types", "OptionalInput")
                .parameterizedBy(kotlinFieldType.copy(nullable = false))
        } else {
            kotlinFieldType
        }

        val inputPropertySpecBuilder = PropertySpec.builder(fieldName, inputFieldType)
            .initializer(fieldName)
        fieldDefinition.description?.content?.let { kdoc ->
            inputPropertySpecBuilder.addKdoc("%L", kdoc)
        }

        val inputPropertySpec = inputPropertySpecBuilder.build()
        inputObjectTypeSpecBuilder.addProperty(inputPropertySpec)

        val inputParameterSpec = ParameterSpec.builder(inputPropertySpec.name, inputPropertySpec.type)
        if (kotlinFieldType.isNullable) {
            if (context.serializer == GraphQLSerializer.JACKSON) {
                inputParameterSpec.defaultValue("%M", MemberName("com.expediagroup.graphql.client.jackson.types", "OptionalInput.Undefined"))
            } else {
                inputParameterSpec.defaultValue("null")
            }
        }
        constructorBuilder.addParameter(inputParameterSpec.build())
    }
    inputObjectTypeSpecBuilder.primaryConstructor(constructorBuilder.build())

    return inputObjectTypeSpecBuilder.build()
}
