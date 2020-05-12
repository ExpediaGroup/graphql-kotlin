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

package com.expediagroup.graphql.federation.directives

import com.expediagroup.graphql.annotations.GraphQLDirective
import graphql.introspection.Introspection.DirectiveLocation

/**
 * ```graphql
 * directive @external on FIELD_DEFINITION
 * ```
 *
 * The @external directive is used to mark a field as owned by another service. This allows service A to use fields from service B while also knowing at runtime the types of that field. @external
 * directive is only applicable on federated extended types. All the external fields should either be referenced from the @key, @requires or @provides directives field sets.
 *
 * Example:
 * Given
 *
 * ```kotlin
 * @KeyDirective(FieldSet("id"))
 * @ExtendsDirective
 * class Product(@ExternalDirective val id: String) {
 *   fun newFunctionality(): String = "whatever"
 * }
 * ```
 *
 * should generate
 *
 * ```graphql
 * type Product @extends @key(fields : "id") {
 *   id: String! @external
 *   newFunctionality: String!
 * }
 * ```
 *
 * @see ExtendsDirective
 * @see KeyDirective
 * @see RequiresDirective
 */
@GraphQLDirective(
    name = EXTERNAL_DIRECTIVE_NAME,
    description = EXTERNAL_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.FIELD_DEFINITION]
)
annotation class ExternalDirective

internal const val EXTERNAL_DIRECTIVE_NAME = "external"
private const val EXTERNAL_DIRECTIVE_DESCRIPTION = "Marks target field as external meaning it will be resolved by federated schema"

internal val EXTERNAL_DIRECTIVE_TYPE: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(EXTERNAL_DIRECTIVE_NAME)
    .description(EXTERNAL_DIRECTIVE_DESCRIPTION)
    .validLocations(DirectiveLocation.FIELD_DEFINITION)
    .build()
