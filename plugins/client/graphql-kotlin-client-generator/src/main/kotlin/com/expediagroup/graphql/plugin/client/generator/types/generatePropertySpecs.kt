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
import com.expediagroup.graphql.plugin.client.generator.ScalarConverterInfo
import com.expediagroup.graphql.plugin.client.generator.exceptions.DeprecatedFieldsSelectedException
import com.expediagroup.graphql.plugin.client.generator.exceptions.InvalidSelectionSetException
import com.expediagroup.graphql.plugin.client.generator.exceptions.MissingArgumentException
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import graphql.Directives.DeprecatedDirective
import graphql.language.Field
import graphql.language.FieldDefinition
import graphql.language.NonNullType
import graphql.language.SelectionSet
import graphql.language.StringValue
import kotlinx.serialization.Serializable

/**
 * Generate [PropertySpec]s from the field definitions and selection set.
 */
internal fun generatePropertySpecs(
    context: GraphQLClientGeneratorContext,
    objectName: String,
    selectionSet: SelectionSet,
    fieldDefinitions: List<FieldDefinition>,
    abstract: Boolean = false
): List<PropertySpec> = selectionSet.getSelectionsOfType(Field::class.java)
    .filterNot { it.name == "__typename" }
    .map { selectedField ->
        val fieldDefinition = fieldDefinitions.find { it.name == selectedField.name }
            ?: throw InvalidSelectionSetException(context.operationName, selectedField.name, objectName)

        val missingRequiredArguments = fieldDefinition.inputValueDefinitions.filter { it.type is NonNullType }
            .map { it.name }
            .minus(selectedField.arguments.map { it.name })
        if (missingRequiredArguments.isNotEmpty()) {
            throw MissingArgumentException(context.operationName, objectName, selectedField.name, missingRequiredArguments)
        }

        val nullable = fieldDefinition.type !is NonNullType
        val kotlinFieldType = generateTypeName(context, fieldDefinition.type, selectedField.selectionSet)
        val fieldName = selectedField.alias ?: fieldDefinition.name

        val propertySpecBuilder = PropertySpec.builder(fieldName, kotlinFieldType.copy(nullable = nullable))
        if (!abstract) {
            propertySpecBuilder.initializer(fieldName)

            val (unwrappedFieldType, isList) = if (kotlinFieldType is ParameterizedTypeName) {
                kotlinFieldType.typeArguments.first() to true
            } else {
                kotlinFieldType to false
            }

            if (context.isCustomScalar(unwrappedFieldType)) {
                val converterInfo = context.scalarClassToConverterTypeSpecs[unwrappedFieldType]
                when {
                    converterInfo is ScalarConverterInfo.JacksonConvertersInfo -> {
                        val annotationMember = if (isList) {
                            "contentConverter"
                        } else {
                            "converter"
                        }
                        propertySpecBuilder.addAnnotation(
                            AnnotationSpec.builder(JsonSerialize::class)
                                .addMember("$annotationMember = %T::class", converterInfo.serializerClassName)
                                .build()
                        )
                        propertySpecBuilder.addAnnotation(
                            AnnotationSpec.builder(JsonDeserialize::class)
                                .addMember("$annotationMember = %T::class", converterInfo.deserializerClassName)
                                .build()
                        )
                    }
                    converterInfo is ScalarConverterInfo.KotlinxSerializerInfo && !isList -> {
                        propertySpecBuilder.addAnnotation(
                            AnnotationSpec.builder(Serializable::class)
                                .addMember("with = %T::class", converterInfo.serializerClassName)
                                .build()
                        )
                    }
                }
            }
        } else {
            propertySpecBuilder.addModifiers(KModifier.ABSTRACT)
        }
        val deprecatedDirective = fieldDefinition.getDirectives(DeprecatedDirective.name).firstOrNull()
        if (deprecatedDirective != null) {
            if (!context.allowDeprecated) {
                throw DeprecatedFieldsSelectedException(context.operationName, selectedField.name, objectName)
            } else {
                val deprecatedReason = deprecatedDirective.getArgument("reason")?.value as? StringValue
                val reason = deprecatedReason?.value ?: "no longer supported"
                propertySpecBuilder.addAnnotation(
                    AnnotationSpec.builder(Deprecated::class)
                        .addMember("message = %S", reason)
                        .build()
                )
            }
        }
        fieldDefinition.description?.content?.let { kdoc ->
            propertySpecBuilder.addKdoc("%L", kdoc)
        }
        propertySpecBuilder.build()
    }
