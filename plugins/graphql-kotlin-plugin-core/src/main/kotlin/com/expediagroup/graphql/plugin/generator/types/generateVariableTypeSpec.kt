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
import graphql.language.VariableDefinition

/**
 * Generate [TypeSpec] data class wrapper for variables used within the target query.
 */
internal fun generateVariableTypeSpec(context: GraphQLClientGeneratorContext, variableDefinitions: List<VariableDefinition>): TypeSpec? {
    val variableTypeSpec = TypeSpec.classBuilder("Variables")
        .addModifiers(KModifier.DATA)

    val constructorSpec = FunSpec.constructorBuilder()
    variableDefinitions.forEach { variableDef ->
        val kotlinTypeName = generateTypeName(context, variableDef.type)

        constructorSpec.addParameter(variableDef.name, kotlinTypeName)
        variableTypeSpec.addProperty(
            PropertySpec.builder(variableDef.name, kotlinTypeName)
                .initializer(variableDef.name)
                .build()
        )
    }

    variableTypeSpec.primaryConstructor(constructorSpec.build())
    return if (variableTypeSpec.propertySpecs.isEmpty()) {
        null
    } else {
        variableTypeSpec.build()
    }
}
