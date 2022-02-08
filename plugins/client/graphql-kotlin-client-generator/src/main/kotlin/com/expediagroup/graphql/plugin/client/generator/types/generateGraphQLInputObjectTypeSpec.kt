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
import graphql.language.Type
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
        val (inputPropertySpec, defaultValue) = createInputPropertySpec(context, fieldDefinition.name, fieldDefinition.type, fieldDefinition.description?.content)
        inputObjectTypeSpecBuilder.addProperty(inputPropertySpec)

        constructorBuilder.addParameter(
            ParameterSpec.builder(inputPropertySpec.name, inputPropertySpec.type)
                .also { builder ->
                    if (defaultValue != null) {
                        builder.defaultValue(defaultValue)
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

internal fun createInputPropertySpec(
    context: GraphQLClientGeneratorContext,
    graphqlFieldName: String,
    graphqlFieldType: Type<*>,
    graphqlFieldDescription: String? = null
): Pair<PropertySpec, CodeBlock?> {
    val kotlinFieldTypeName = generateTypeName(context, graphqlFieldType)

    val (rawType, isList) = unwrapRawType(kotlinFieldTypeName)
    val isCustomScalar = context.isCustomScalar(rawType)
    val shouldWrapInOptional = shouldWrapInOptional(kotlinFieldTypeName, context)

    val scalarAnnotations = if (isCustomScalar) {
        generateCustomScalarPropertyAnnotations(context, rawType, isList, shouldWrapInOptional)
    } else {
        emptyList()
    }

    val inputFieldType = if (shouldWrapInOptional) {
        if (context.serializer == GraphQLSerializer.KOTLINX) {
            kotlinFieldTypeName.copy(annotations = scalarAnnotations).wrapOptionalInputType(context)
        } else {
            kotlinFieldTypeName.wrapOptionalInputType(context)
        }
    } else {
        kotlinFieldTypeName
    }

    val inputProperty = PropertySpec.builder(graphqlFieldName, inputFieldType)
        .initializer(graphqlFieldName)
        .also { builder ->
            if (graphqlFieldDescription != null) {
                builder.addKdoc("%L", graphqlFieldDescription)
            }

            if (isCustomScalar) {
                if (shouldWrapInOptional) {
                    // annotations were already set for KOTLINX optional scalar
                    if (context.serializer == GraphQLSerializer.JACKSON) {
                        context.requireJacksonOptionalScalarSerializer = true
                        builder.addAnnotations(scalarAnnotations)
                    }
                } else {
                    builder.addAnnotations(scalarAnnotations)
                }
            }
        }
        .build()
    val defaultValue = if (kotlinFieldTypeName.isNullable) {
        nullableDefaultValueCodeBlock(context)
    } else {
        null
    }
    return inputProperty to defaultValue
}

internal fun nullableDefaultValueCodeBlock(context: GraphQLClientGeneratorContext): CodeBlock = if (context.useOptionalInputWrapper) {
    if (context.serializer == GraphQLSerializer.JACKSON) {
        CodeBlock.of("%M", MemberName("com.expediagroup.graphql.client.jackson.types", "OptionalInput.Undefined"))
    } else {
        CodeBlock.of("%M", MemberName("com.expediagroup.graphql.client.serialization.types", "OptionalInput.Undefined"))
    }
} else {
    CodeBlock.of("null")
}
