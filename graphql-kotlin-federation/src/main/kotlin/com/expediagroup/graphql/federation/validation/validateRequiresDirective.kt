/*
 * Copyright 2019 Expedia, Inc
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
import graphql.schema.GraphQLFieldDefinition

// [OK]    @requires references valid fields marked @external
// [ERROR] @requires specified on base type
// [ERROR] @requires specifies non-existent fields
internal fun validateRequiresDirective(validatedType: String, validatedField: GraphQLFieldDefinition, fieldMap: Map<String, GraphQLFieldDefinition>, extendedType: Boolean): List<String> {
    val errors = mutableListOf<String>()
    if (extendedType) {
        errors.addAll(validateDirective("$validatedType.${validatedField.name}", FederationDirectives.requiresName, validatedField.directivesByName, fieldMap, extendedType))
    } else {
        errors.add("base $validatedType type has fields marked with @requires directive, validatedField=${validatedField.name}")
    }
    return errors
}
