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
import graphql.introspection.Introspection.DirectiveLocation

/**
 * **`@extends` directive is deprecated**. Federation v2 no longer requires `@extends` directive due to the smart entity type
 * merging. All usage of `@extends` directive should be removed from your Federation v2 schemas.
 *
 * ```graphql
 * directive @extends on OBJECT | INTERFACE
 * ```
 *
 * Extends directive is used to represent type extensions in the schema. Native type extensions are currently unsupported by the graphql-kotlin libraries. Federated extended types should have
 * corresponding @key directive defined that specifies primary key required to fetch the underlying object.
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
 * @see KeyDirective
 */
@Deprecated(message = "@extends is only required in Federation v1 and can be safely omitted from Federation v2 schemas")
@GraphQLDirective(
    name = EXTENDS_DIRECTIVE_NAME,
    description = DESCRIPTION,
    locations = [DirectiveLocation.OBJECT, DirectiveLocation.INTERFACE]
)
annotation class ExtendsDirective

internal const val EXTENDS_DIRECTIVE_NAME = "extends"
private const val DESCRIPTION = "Marks target object as extending part of the federated schema"

internal val EXTENDS_DIRECTIVE_TYPE: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(EXTENDS_DIRECTIVE_NAME)
    .description(DESCRIPTION)
    .validLocations(DirectiveLocation.OBJECT, DirectiveLocation.INTERFACE)
    .build()
