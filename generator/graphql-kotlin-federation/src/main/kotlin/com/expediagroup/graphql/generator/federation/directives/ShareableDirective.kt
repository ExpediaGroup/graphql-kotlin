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
 * directive @shareable repeatable on FIELD_DEFINITION | OBJECT
 * ```
 *
 * Shareable directive indicates that given object and/or field can be resolved by multiple subgraphs. If an object is marked as `@shareable` then all its fields are automatically shareable without the
 * need for explicitly marking them with `@shareable` directive. All fields referenced from `@key` directive are automatically shareable as well.
 *
 * >NOTE: Objects/fields have to specify same shareability (i.e. `@shareable` or not) mode across ALL subgraphs.
 *
 * Example:
 *
 * ```graphql
 * type Product @key(fields: "id") {
 *   id: ID!                           # shareable because id is a key field
 *   name: String                      # non-shareable
 *   description: String @shareable    # shareable
 * }
 *
 * type User @key(fields: "email") @shareable {
 *   email: String                    # shareable because User is marked shareable
 *   name: String                     # shareable because User is marked shareable
 * }
 * ```
 */
@Repeatable
@GraphQLDirective(
    name = SHAREABLE_DIRECTIVE_NAME,
    description = SHAREABLE_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.FIELD_DEFINITION, DirectiveLocation.OBJECT]
)
annotation class ShareableDirective

internal const val SHAREABLE_DIRECTIVE_NAME = "shareable"
private const val SHAREABLE_DIRECTIVE_DESCRIPTION = "Indicates that given object and/or field can be resolved by multiple subgraphs"

internal val SHAREABLE_DIRECTIVE_TYPE: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(SHAREABLE_DIRECTIVE_NAME)
    .description(SHAREABLE_DIRECTIVE_DESCRIPTION)
    .validLocations(DirectiveLocation.FIELD_DEFINITION, DirectiveLocation.OBJECT)
    .repeatable(true)
    .build()
