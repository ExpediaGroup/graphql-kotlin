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
import com.expediagroup.graphql.generator.federation.directives.PROVIDES_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.REQUIRES_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.exception.InvalidFederatedSchema
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLTypeUtil

/**
 * Validates generated federated objects.
 */
internal class FederatedSchemaValidator {

    /**
     * Validates target GraphQLType whether it is a valid federated object.
     *
     * Verifies:
     * - @key, @provides and @requires field sets reference existing fields
     * - @requires references @external fields
     * - @provides references an object
     * - field sets cannot reference unions
     * - list and interfaces can only be referenced from `@requires` and `@provides`
     */
    internal fun validateGraphQLType(type: GraphQLType) {
        val unwrappedType = GraphQLTypeUtil.unwrapAll(type)
        if (unwrappedType is GraphQLObjectType && unwrappedType.isFederatedType()) {
            validate(unwrappedType.name, unwrappedType.fieldDefinitions, unwrappedType.allAppliedDirectivesByName)
        } else if (unwrappedType is GraphQLInterfaceType && unwrappedType.isFederatedType()) {
            validate(unwrappedType.name, unwrappedType.fieldDefinitions, unwrappedType.allAppliedDirectivesByName)
        }
    }

    private fun validate(federatedType: String, fields: List<GraphQLFieldDefinition>, directiveMap: Map<String, List<GraphQLAppliedDirective>>) {
        val errors = mutableListOf<String>()
        val fieldMap = fields.associateBy { it.name }

        errors.addAll(validateDirective(federatedType, KEY_DIRECTIVE_NAME, directiveMap, fieldMap))
        for (field in fields) {
            if (field.getAppliedDirective(REQUIRES_DIRECTIVE_NAME) != null) {
                errors.addAll(validateDirective("$federatedType.${field.name}", REQUIRES_DIRECTIVE_NAME, field.allAppliedDirectivesByName, fieldMap))
            }

            if (field.getAppliedDirective(PROVIDES_DIRECTIVE_NAME) != null) {
                when (val returnType = GraphQLTypeUtil.unwrapAll(field.type)) {
                    is GraphQLObjectType -> {
                        val returnTypeFields = returnType.fieldDefinitions.associateBy { it.name }
                        errors.addAll(
                            validateDirective(
                                "$federatedType.${field.name}",
                                PROVIDES_DIRECTIVE_NAME,
                                field.allAppliedDirectivesByName,
                                returnTypeFields
                            )
                        )
                    }
                    // skip validation for nested object types as they are still under construction
                    is GraphQLTypeReference -> continue
                    else -> errors.add("@provides directive is specified on a $federatedType.${field.name} field but it does not return an object type")
                }
            }
        }

        if (errors.isNotEmpty()) {
            throw InvalidFederatedSchema(errors)
        }
    }

    private fun GraphQLDirectiveContainer.isFederatedType() = this.getAppliedDirectives(KEY_DIRECTIVE_NAME).isNotEmpty()
}
