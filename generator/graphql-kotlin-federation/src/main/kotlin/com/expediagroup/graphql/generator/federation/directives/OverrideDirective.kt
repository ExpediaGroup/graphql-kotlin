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
import graphql.Scalars
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLNonNull

/**
 * ```graphql
 * directive @override(from: String!) on FIELD_DEFINITION
 * ```
 *
 * The @override directive is used to indicate that the current subgraph is taking responsibility for resolving the marked field away from the subgraph specified in the from argument. Name of the subgraph
 * to be overridden has to match the name of the subgraph that was used to publish their schema.
 *
 * >NOTE: Only one subgraph can `@override` any given field. If multiple subgraphs attempt to `@override` the same field, a composition error occurs.
 *
 * @param from name of the subgraph to override field resolution
 *
 * @see <a href="https://www.apollographql.com/docs/rover/subgraphs/#publishing-a-subgraph-schema-to-apollo-studio">Publishing schema to Apollo Studio</a>
 */
@GraphQLDirective(
    name = OVERRIDE_DIRECTIVE_NAME,
    description = OVERRIDE_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.FIELD_DEFINITION]
)
annotation class OverrideDirective(val from: String)

internal const val OVERRIDE_DIRECTIVE_NAME = "override"
private const val OVERRIDE_DIRECTIVE_DESCRIPTION = "Overrides fields resolution logic from other subgraph. Used for migrating fields from one subgraph to another."

internal val OVERRIDE_DIRECTIVE_TYPE: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(OVERRIDE_DIRECTIVE_NAME)
    .description(OVERRIDE_DIRECTIVE_DESCRIPTION)
    .validLocations(DirectiveLocation.FIELD_DEFINITION)
    .argument(
        GraphQLArgument.newArgument()
            .name("from")
            .type(GraphQLNonNull.nonNull(Scalars.GraphQLString))
    )
    .build()
