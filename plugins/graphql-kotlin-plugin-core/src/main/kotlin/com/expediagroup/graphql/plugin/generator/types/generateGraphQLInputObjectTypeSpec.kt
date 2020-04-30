/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.InputObjectTypeDefinition

/**
 * Generate [TypeSpec] data class from the specified input object definition where are fields are mapped to corresponding Kotlin property.
 */
internal fun generateGraphQLInputObjectTypeSpec(context: GraphQLClientGeneratorContext, inputObjectDefinition: InputObjectTypeDefinition): TypeSpec {
    val inputObjectTypeSpecBuilder = TypeSpec.classBuilder(inputObjectDefinition.name)
    inputObjectTypeSpecBuilder.modifiers.add(KModifier.DATA)
    inputObjectDefinition.description?.content?.let { kdoc ->
        inputObjectTypeSpecBuilder.addKdoc(kdoc)
    }

    val constructorBuilder = FunSpec.constructorBuilder()
    inputObjectDefinition.inputValueDefinitions.forEach { fieldDefinition ->
        val kotlinFieldType = generateTypeName(context, fieldDefinition.type)
        val fieldName = fieldDefinition.name

        val inputPropertySpecBuilder = PropertySpec.builder(fieldName, kotlinFieldType)
            .initializer(fieldName)
        fieldDefinition.description?.content?.let { kdoc ->
            inputPropertySpecBuilder.addKdoc(kdoc)
        }

        val inputPropertySpec = inputPropertySpecBuilder.build()
        inputObjectTypeSpecBuilder.addProperty(inputPropertySpec)
        constructorBuilder.addParameter(inputPropertySpec.name, inputPropertySpec.type)
    }
    inputObjectTypeSpecBuilder.primaryConstructor(constructorBuilder.build())

    val inputTypeObjectTypeSpec = inputObjectTypeSpecBuilder.build()
    context.typeSpecs[inputObjectDefinition.name] = inputTypeObjectTypeSpec
    return inputTypeObjectTypeSpec
}
