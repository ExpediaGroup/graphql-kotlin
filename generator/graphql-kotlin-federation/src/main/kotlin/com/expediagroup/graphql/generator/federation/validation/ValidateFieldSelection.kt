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

import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_NAME
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeUtil
import graphql.schema.GraphQLUnionType

internal fun validateFieldSelection(validatedDirective: DirectiveInfo, selection: FieldSetSelection, targetType: GraphQLType, errors: MutableList<String>) {
    when (val unwrapped = GraphQLTypeUtil.unwrapNonNull(targetType)) {
        is GraphQLScalarType, is GraphQLEnumType -> {
            if (selection.subSelections.isNotEmpty()) {
                errors.add("$validatedDirective specifies invalid field set - field set specifies selection set on a leaf node, field=${selection.field}")
            }
        }
        is GraphQLUnionType -> errors.add("$validatedDirective specifies invalid field set - field set references GraphQLUnionType, field=${selection.field}")
        is GraphQLList -> {
            if (KEY_DIRECTIVE_NAME == validatedDirective.directiveName) {
                errors.add("$validatedDirective specifies invalid field set - field set references GraphQLList, field=${selection.field}")
            } else {
                validateFieldSelection(validatedDirective, selection, GraphQLTypeUtil.unwrapOne(targetType), errors)
            }
        }
        is GraphQLInterfaceType -> {
            if (KEY_DIRECTIVE_NAME == validatedDirective.directiveName) {
                errors.add("$validatedDirective specifies invalid field set - field set references GraphQLInterfaceType, field=${selection.field}")
            } else if (selection.subSelections.isEmpty()) {
                errors.add("$validatedDirective specifies invalid field set - ${selection.field} interface does not specify selection set")
            } else {
                validateFieldSetSelection(
                    validatedDirective,
                    selection.subSelections,
                    unwrapped.fieldDefinitions.associateBy { it.name },
                    errors
                )
            }
        }
        is GraphQLObjectType -> {
            if (selection.subSelections.isEmpty()) {
                errors.add("$validatedDirective specifies invalid field set - ${selection.field} object does not specify selection set")
            } else {
                validateFieldSetSelection(
                    validatedDirective,
                    selection.subSelections,
                    unwrapped.fieldDefinitions.associateBy { it.name },
                    errors
                )
            }
        }
    }
}
