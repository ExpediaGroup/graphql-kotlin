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

package com.expediagroup.graphql.generator.federation.directives

import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.federation.types.FIELD_SET_ARGUMENT
import graphql.introspection.Introspection.DirectiveLocation

/**
 * ```graphql
 * directive @requires(fields: _FieldSet!) on FIELD_DEFINITON
 * ```
 *
 * The `@requires` directive is used to specify external (provided by other subgraphs) entity fields that are needed to resolve target field. It is used to develop a query plan where
 * the required fields may not be needed by the client, but the service may need additional information from other subgraphs. Required fields specified in the directive field set should
 * correspond to a valid field on the underlying GraphQL interface/object and should be instrumented with `@external` directive.
 *
 * Fields specified in the `@requires` directive will only be specified in the queries that reference those fields. This is problematic for Kotlin as the non-nullable primitive properties
 * have to be initialized when they are declared. Simplest workaround for this problem is to initialize the underlying property to some default value (e.g. null) that will be used if
 * it is not specified. This approach might become problematic though as it might be impossible to determine whether fields was initialized with the default value or the invalid/default
 * value was provided by the federated query. Another potential workaround is to rely on delegation to initialize the property after the object gets created. This will ensure that exception
 * will be thrown if queries attempt to resolve fields that reference the uninitialized property.
 *
 * Example:
 * Given
 *
 * ```kotlin
 * @KeyDirective(FieldSet("id"))
 * @ExtendsDirective
 * class Product(@ExternalDirective val id: String) {
 *
 *   @ExternalDirective
 *   var weight: Double by Delegates.notNull()
 *
 *   @RequiresDirective(FieldSet("weight"))
 *   fun shippingCost(): String { ... }
 *
 *   fun additionalInfo(): String { ... }
 * }
 * ```
 *
 * should generate
 *
 * ```graphql
 * type Product @extends @key(fields : "id") {
 *   additionalInfo: String!
 *   id: String! @external
 *   shippingCost: String! @requires(fields : "weight")
 *   weight: Float! @external
 * }
 * ```
 *
 * @param fields field set that represents a set of additional external fields required to resolve target field
 *
 * @see FieldSet
 * @see ExternalDirective
 */
@GraphQLDirective(
    name = REQUIRES_DIRECTIVE_NAME,
    description = REQUIRES_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.FIELD_DEFINITION]
)
annotation class RequiresDirective(val fields: FieldSet)

internal const val REQUIRES_DIRECTIVE_NAME = "requires"
private const val REQUIRES_DIRECTIVE_DESCRIPTION = "Specifies required input field set from the base type for a resolver"

internal val REQUIRES_DIRECTIVE_TYPE: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(REQUIRES_DIRECTIVE_NAME)
    .description(REQUIRES_DIRECTIVE_DESCRIPTION)
    .validLocations(DirectiveLocation.FIELD_DEFINITION)
    .argument(FIELD_SET_ARGUMENT)
    .build()
