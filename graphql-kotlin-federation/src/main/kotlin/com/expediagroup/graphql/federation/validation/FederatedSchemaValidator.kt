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

package com.expediagroup.graphql.federation.validation

import com.apollographql.federation.graphqljava.FederationDirectives
import com.expediagroup.graphql.federation.exception.InvalidFederatedSchema
import com.expediagroup.graphql.federation.extensions.isFederatedType
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeUtil

/**
 * Validates generated federated objects.
 */
internal class FederatedSchemaValidator {

    /**
     * Validates target GraphQLType whether it is a valid federated object.
     *
     * Verifies:
     * - base type doesn't declare any @external fields
     * - @key directive references existing fields
     * - @key directive on extended types references @external fields
     * - @requires directive is only applicable on extended types and references @external fields
     * - @provides directive references valid @external fields
     */
    internal fun validateGraphQLType(type: GraphQLType) {
        val unwrappedType = GraphQLTypeUtil.unwrapAll(type)
        if (unwrappedType is GraphQLObjectType && unwrappedType.isFederatedType()) {
            validate(unwrappedType.name, unwrappedType.fieldDefinitions, unwrappedType.directivesByName)
        } else if (unwrappedType is GraphQLInterfaceType && unwrappedType.isFederatedType()) {
            validate(unwrappedType.name, unwrappedType.fieldDefinitions, unwrappedType.directivesByName)
        }
    }

    private fun validate(federatedType: String, fields: List<GraphQLFieldDefinition>, directives: Map<String, GraphQLDirective>) {
        val errors = mutableListOf<String>()
        val fieldMap = fields.associateBy { it.name }
        val extendedType = directives.containsKey(FederationDirectives.extendsName)

        // [OK]    @key directive is specified
        // [OK]    @key references valid existing fields
        // [OK]    @key on @extended type references @external fields
        // [ERROR] @key references fields resulting in list
        // [ERROR] @key references fields resulting in union
        // [ERROR] @key references fields resulting in interface
        errors.addAll(validateDirective(federatedType, FederationDirectives.keyName, directives, fieldMap, extendedType))

        for (field in fields) {
            if (field.getDirective(FederationDirectives.requiresName) != null) {
                errors.addAll(validateRequiresDirective(federatedType, field, fieldMap, extendedType))
            }

            if (field.getDirective(FederationDirectives.providesName) != null) {
                errors.addAll(validateProvidesDirective(federatedType, field))
            }
        }

        // [ERROR] federated base type references @external fields
        if (!extendedType) {
            val externalFields = fields.filter { it.getDirective(FederationDirectives.externalName) != null }.map { it.name }
            if (externalFields.isNotEmpty()) {
                errors.add("base $federatedType type has fields marked with @external directive, fields=$externalFields")
            }
        }

        if (errors.isNotEmpty()) {
            throw InvalidFederatedSchema(errors)
        }
    }
}
