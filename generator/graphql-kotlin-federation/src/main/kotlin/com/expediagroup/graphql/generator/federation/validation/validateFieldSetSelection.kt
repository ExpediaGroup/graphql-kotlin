/*
 * Copyright 2023 Expedia, Inc
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

import com.expediagroup.graphql.generator.federation.directives.EXTERNAL_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.REQUIRES_DIRECTIVE_NAME
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLTypeUtil

internal fun validateFieldSetSelection(
    validatedDirective: DirectiveInfo,
    selections: List<FieldSetSelection>,
    fields: Map<String, GraphQLFieldDefinition>,
    errors: MutableList<String>,
    isExternalPath: Boolean = false
) {
    for (selection in selections) {
        val currentField = fields[selection.field]
        if (currentField == null) {
            errors.add("$validatedDirective specifies invalid field set - field set specifies field that does not exist, field=${selection.field}")
        } else {
            val currentFieldType = currentField.type
            val isExternal = isExternalPath || GraphQLTypeUtil.unwrapAll(currentFieldType).isExternalPath() || currentField.isExternalType()
            if (REQUIRES_DIRECTIVE_NAME == validatedDirective.directiveName && GraphQLTypeUtil.isLeaf(currentFieldType) && !isExternal) {
                errors.add("$validatedDirective specifies invalid field set - @requires should reference only @external fields, field=${selection.field}")
            }
            validateFieldSelection(validatedDirective, selection, currentFieldType, errors, isExternal)
        }
    }
}

private fun GraphQLDirectiveContainer.isExternalType(): Boolean = this.getAppliedDirectives(EXTERNAL_DIRECTIVE_NAME).isNotEmpty()
private fun GraphQLNamedType.isExternalPath(): Boolean = this is GraphQLDirectiveContainer && this.isExternalType()
