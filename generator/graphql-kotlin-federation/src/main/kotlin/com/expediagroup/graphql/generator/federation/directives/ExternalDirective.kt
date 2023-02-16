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

package com.expediagroup.graphql.generator.federation.directives

import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import graphql.introspection.Introspection.DirectiveLocation

/**
 * ```graphql
 * # federation v1 definition
 * directive @external on FIELD_DEFINITION
 *
 * # federation v2 definition
 * directive @external on OBJECT | FIELD_DEFINITION
 * ```
 *
 * The @external directive is used to mark a field as owned by another service. This allows service A to use fields from service B while also knowing at runtime the types of that field. @external
 * directive is only applicable on federated extended types. All the external fields should either be referenced from the @key, @requires or @provides directives field sets.
 *
 * Due to the smart merging of entity types, Federation v2 no longer requires `@external` directive on `@key` fields and can be safely omitted from the schema. `@external` directive is only required
 * on fields referenced by the `@requires` and `@provides` directive.
 *
 * Example:
 * Given
 *
 * ```kotlin
 * @KeyDirective(FieldSet("id"))
 * class Product(val id: String) {
 *   @ExternalDirective
 *   var externalField: String by Delegates.notNull()
 *
 *   @RequiresDirective(FieldSet("externalField"))
 *   fun newFunctionality(): String { ... }
 * }
 * ```
 *
 * should generate
 *
 * ```graphql
 * type Product @key(fields : "id") {
 *   externalField: String! @external
 *   id: String!
 *   newFunctionality: String!
 * }
 * ```
 *
 * @see KeyDirective
 * @see RequiresDirective
 */
@GraphQLDirective(
    name = EXTERNAL_DIRECTIVE_NAME,
    description = EXTERNAL_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.OBJECT, DirectiveLocation.FIELD_DEFINITION]
)
annotation class ExternalDirective

internal const val EXTERNAL_DIRECTIVE_NAME = "external"
private const val EXTERNAL_DIRECTIVE_DESCRIPTION = "Marks target field as external meaning it will be resolved by federated schema"

internal val EXTERNAL_DIRECTIVE_TYPE: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(EXTERNAL_DIRECTIVE_NAME)
    .description(EXTERNAL_DIRECTIVE_DESCRIPTION)
    .validLocations(DirectiveLocation.FIELD_DEFINITION)
    .build()

internal val EXTERNAL_DIRECTIVE_TYPE_V2: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(EXTERNAL_DIRECTIVE_NAME)
    .description(EXTERNAL_DIRECTIVE_DESCRIPTION)
    .validLocations(DirectiveLocation.OBJECT, DirectiveLocation.FIELD_DEFINITION)
    .build()
