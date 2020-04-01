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

import com.expediagroup.graphql.directives.DEPRECATED_DIRECTIVE_NAME
import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.PropertySpec
import graphql.language.Field
import graphql.language.FieldDefinition
import graphql.language.NonNullType
import graphql.language.SelectionSet
import graphql.language.StringValue

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
            ?: throw RuntimeException("unable to find corresponding field definition of ${selectedField.name} in $objectName")

        val nullable = fieldDefinition.type !is NonNullType
        val kotlinFieldType = generateTypeName(context, fieldDefinition.type, selectedField.selectionSet)
        val fieldName = selectedField.alias ?: fieldDefinition.name

        val propertySpecBuilder = PropertySpec.builder(fieldName, kotlinFieldType.copy(nullable = nullable))
        if (!abstract) {
            propertySpecBuilder.initializer(fieldName)
        }
        fieldDefinition.getDirective(DEPRECATED_DIRECTIVE_NAME)?.let { deprecatedDirective ->
            if (!context.allowDeprecated) {
                throw RuntimeException("query specifies deprecated field - ${selectedField.name} in $objectName, update your query or update your configuration to allow usage of deprecated fields")
            } else {
                val deprecatedReason = deprecatedDirective.getArgument("reason")?.value as? StringValue
                val reason = deprecatedReason?.value ?: "no longer supported"
                propertySpecBuilder.addAnnotation(AnnotationSpec.builder(Deprecated::class)
                    .addMember("message = %S", reason)
                    .build())
            }
        }
        fieldDefinition.description?.content?.let { kdoc ->
            propertySpecBuilder.addKdoc(kdoc)
        }
        propertySpecBuilder.build()
    }
