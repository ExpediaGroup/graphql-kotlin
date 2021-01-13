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

package com.expediagroup.graphql.generator.federation.validation

import com.expediagroup.graphql.generator.federation.directives.FIELD_SET_ARGUMENT_NAME
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition

internal fun validateDirective(
    validatedType: String,
    targetDirective: String,
    directives: Map<String, GraphQLDirective>,
    fieldMap: Map<String, GraphQLFieldDefinition>,
    extendedType: Boolean
): List<String> {
    val validationErrors = mutableListOf<String>()
    val directive = directives[targetDirective]

    if (directive == null) {
        validationErrors.add("@$targetDirective directive is missing on federated $validatedType type")
    } else {
        val fieldSetValue = (directive.getArgument(FIELD_SET_ARGUMENT_NAME)?.value as? FieldSet)?.value
        val fieldSet = fieldSetValue?.split(" ")?.filter { it.isNotEmpty() }.orEmpty()
        if (fieldSet.isEmpty()) {
            validationErrors.add("@$targetDirective directive on $validatedType is missing field information")
        } else {
            // validate directive field set selection
            val directiveInfo = DirectiveInfo(
                directiveName = targetDirective,
                fieldSet = fieldSet.joinToString(" "),
                typeName = validatedType
            )
            validateFieldSelection(directiveInfo, fieldSet.iterator(), fieldMap, extendedType, validationErrors)
        }
    }
    return validationErrors
}
