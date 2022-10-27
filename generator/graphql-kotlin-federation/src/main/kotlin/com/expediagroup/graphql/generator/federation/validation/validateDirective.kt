/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.validation

import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.exception.InvalidFederatedSchema
import com.expediagroup.graphql.generator.federation.types.FIELD_SET_ARGUMENT_NAME
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLFieldDefinition

internal fun validateDirective(
    validatedType: String,
    targetDirective: String,
    directiveMap: Map<String, List<GraphQLAppliedDirective>>,
    fieldMap: Map<String, GraphQLFieldDefinition>,
): List<String> {
    val validationErrors = mutableListOf<String>()
    val directives = directiveMap[targetDirective]

    if (directives == null) {
        validationErrors.add("@$targetDirective directive is missing on federated $validatedType type")
    } else {
        for (directive in directives) {
            val fieldSetValue = (directive.getArgument(FIELD_SET_ARGUMENT_NAME)?.argumentValue?.value as? FieldSet)?.value ?: ""
            val fieldSet = fieldSetValue.split(" ").filter { it.isNotEmpty() }
            if (fieldSet.isEmpty()) {
                validationErrors.add("@$targetDirective directive on $validatedType is missing field information")
            } else {
                val directiveInfo = DirectiveInfo(
                    directiveName = targetDirective,
                    fieldSet = fieldSetValue,
                    typeName = validatedType
                )
                validateFieldSet(directiveInfo, fieldSet)
                val selections = parseFieldSet(directiveInfo, fieldSet.iterator())
                validateFieldSetSelection(directiveInfo, selections, fieldMap, validationErrors)
            }
        }
    }
    return validationErrors
}

private fun validateFieldSet(directiveInfo: DirectiveInfo, fieldSet: List<String>) {
    var isOpen = 0
    for (field in fieldSet) {
        when (field) {
            "{" -> isOpen++
            "}" -> isOpen--
        }

        if (isOpen < 0) {
            break
        }
    }

    if (isOpen != 0) {
        throw InvalidFederatedSchema(listOf("$directiveInfo specifies malformed field set: ${directiveInfo.fieldSet}"))
    }
}

internal fun parseFieldSet(directiveInfo: DirectiveInfo, iterator: Iterator<String>): List<FieldSetSelection> {
    val selections = mutableListOf<FieldSetSelection>()
    var previous: FieldSetSelection? = null
    while (iterator.hasNext()) {
        when (val currentField = iterator.next()) {
            "{" -> previous?.subSelections?.addAll(parseFieldSet(directiveInfo, iterator))
            "}" -> break
            else -> {
                val current = FieldSetSelection(currentField)
                selections.add(current)

                previous = current
            }
        }
    }
    return selections
}
