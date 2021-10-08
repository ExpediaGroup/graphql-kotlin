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

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGeneratorContext
import com.expediagroup.graphql.plugin.client.generator.GraphQLSerializer
import com.expediagroup.graphql.plugin.client.generator.LOGGER
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.InputObjectTypeDefinition
import kotlinx.serialization.Serializable

/**
 * Generate [TypeSpec] data class from the specified input object definition where are fields are mapped to corresponding Kotlin property.
 */
internal fun generateGraphQLInputObjectTypeSpec(context: GraphQLClientGeneratorContext, inputObjectDefinition: InputObjectTypeDefinition): TypeSpec {
    val inputTypeName = inputObjectDefinition.name
    val inputObjectTypeSpecBuilder = TypeSpec.classBuilder(inputTypeName)
        .addModifiers(KModifier.DATA)
        .addAnnotation(Generated::class)
    inputObjectDefinition.description?.content?.let { kdoc ->
        inputObjectTypeSpecBuilder.addKdoc("%L", kdoc)
    }

    if (context.serializer == GraphQLSerializer.KOTLINX) {
        inputObjectTypeSpecBuilder.addAnnotation(Serializable::class)
    }

    val constructorBuilder = FunSpec.constructorBuilder()
    inputObjectDefinition.inputValueDefinitions.forEach { fieldDefinition ->
        val kotlinFieldTypeName = generateTypeName(context, fieldDefinition.type)

        val (rawType, isList) = unwrapRawType(kotlinFieldTypeName)
        val isCustomScalar = context.isCustomScalar(rawType)
        val shouldWrapInOptional = shouldWrapInOptional(kotlinFieldTypeName, context)

        val inputFieldType = if (!isCustomScalar && shouldWrapInOptional) {
            kotlinFieldTypeName.wrapOptionalInputType(context)
        } else {
            kotlinFieldTypeName
        }

        val inputPropertySpec = PropertySpec.builder(fieldDefinition.name, inputFieldType)
            .initializer(fieldDefinition.name)
            .also { builder ->
                fieldDefinition.description?.content?.let { kdoc ->
                    builder.addKdoc("%L", kdoc)
                }

                if (isCustomScalar) {
                    builder.addAnnotations(generateCustomScalarPropertyAnnotations(context, rawType, isList))

                    if (shouldWrapInOptional) {
                        LOGGER.warn(
                            "Operation ${context.operationName} specifies optional custom scalar as input - ${fieldDefinition.name} in Variables. " +
                                "Currently custom scalars do not work with optional wrappers."
                        )
                        builder.addKdoc("\nNOTE: This field was not wrapped in optional as currently custom scalars do not work with optional wrappers.")
                    }
                }
            }
            .build()
        inputObjectTypeSpecBuilder.addProperty(inputPropertySpec)

        constructorBuilder.addParameter(
            ParameterSpec.builder(inputPropertySpec.name, inputPropertySpec.type)
                .also { builder ->
                    if (kotlinFieldTypeName.isNullable) {
                        builder.defaultValue(nullableDefaultValueCodeBlock(context, isCustomScalar))
                    }
                }
                .build()
        )
    }
    inputObjectTypeSpecBuilder.primaryConstructor(constructorBuilder.build())

    return inputObjectTypeSpecBuilder.build()
}

internal fun shouldWrapInOptional(type: TypeName, context: GraphQLClientGeneratorContext) = type.isNullable && context.useOptionalInputWrapper

internal fun TypeName.wrapOptionalInputType(context: GraphQLClientGeneratorContext): TypeName =
    if (context.serializer == GraphQLSerializer.JACKSON) {
        ClassName("com.expediagroup.graphql.client.jackson.types", "OptionalInput")
            .parameterizedBy(this.copy(nullable = false))
    } else {
        ClassName("com.expediagroup.graphql.client.serialization.types", "OptionalInput")
            .parameterizedBy(this.copy(nullable = false))
    }

internal fun nullableDefaultValueCodeBlock(context: GraphQLClientGeneratorContext, isCustomScalar: Boolean): CodeBlock = if (context.useOptionalInputWrapper && !isCustomScalar) {
    if (context.serializer == GraphQLSerializer.JACKSON) {
        CodeBlock.of("%M", MemberName("com.expediagroup.graphql.client.jackson.types", "OptionalInput.Undefined"))
    } else {
        CodeBlock.of("%M", MemberName("com.expediagroup.graphql.client.serialization.types", "OptionalInput.Undefined"))
    }
} else {
    CodeBlock.of("null")
}
