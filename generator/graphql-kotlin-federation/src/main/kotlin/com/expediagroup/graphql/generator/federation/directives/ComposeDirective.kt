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
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLNonNull

/**
 * ```graphql
 * directive @composeDirective(name: String!) repeatable on SCHEMA
 * ```
 *
 * By default, Supergraph schema excludes all custom directives. The `@composeDirective` is used to specify custom directives that should be exposed in the Supergraph schema.
 *
 * Example:
 * Given `@custom` directive we can preserve it in the Supergraph schema
 *
 * ```kotlin
 * @GraphQLDirective(name = "custom", locations = [Introspection.DirectiveLocation.FIELD_DEFINITION])
 * annotation class CustomDirective
 *
 * @ComposeDirective(name = "custom")
 * class CustomSchema
 *
 * class SimpleQuery {
 *   @CustomDirective
 *   fun helloWorld(): String = "Hello World"
 * }
 * ```
 *
 * it will generate following schema
 *
 * ```graphql
 * schema @composeDirective(name: "@myDirective") @link(import : ["@composeDirective", "@extends", "@external", "@inaccessible", "@interfaceObject", "@key", "@override", "@provides", "@requires", "@shareable", "@tag", "FieldSet"], url : "https://specs.apollo.dev/federation/v2.3"){
 *    query: Query
 * }
 *
 * directive @custom on FIELD_DEFINITION
 *
 * type Query {
 *   helloWorld: String! @custom
 * }
 * ```
 *
 * @see <a href="https://www.apollographql.com/docs/federation/federated-types/federated-directives/#composedirective">@composeDirective definition</a>
 */
@Repeatable
@GraphQLDirective(
    name = COMPOSE_DIRECTIVE_NAME,
    description = COMPOSE_DIRECTIVE_DESCRIPTION,
    locations = [Introspection.DirectiveLocation.SCHEMA]
)
annotation class ComposeDirective(val name: String)

internal const val COMPOSE_DIRECTIVE_NAME = "composeDirective"
private const val COMPOSE_DIRECTIVE_DESCRIPTION = "Marks underlying custom directive to be included in the Supergraph schema"

internal val COMPOSE_DIRECTIVE_TYPE: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(COMPOSE_DIRECTIVE_NAME)
    .description(COMPOSE_DIRECTIVE_DESCRIPTION)
    .validLocations(Introspection.DirectiveLocation.SCHEMA)
    .argument(
        GraphQLArgument.newArgument()
            .name("name")
            .type(GraphQLNonNull.nonNull(Scalars.GraphQLString))
    )
    .repeatable(true)
    .build()
