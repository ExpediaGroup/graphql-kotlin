/*
 * Copyright 2025 Expedia, Inc
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
import com.expediagroup.graphql.generator.directives.DirectiveMetaInformation
import graphql.Scalars
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLAppliedDirectiveArgument
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
 * @param label optional string containing migration parameters (e.g. "percent(number)"). Enterprise feature available in Federation 2.7+.
 *
 * @see <a href="https://www.apollographql.com/docs/rover/subgraphs/#publishing-a-subgraph-schema-to-apollo-studio">Publishing schema to Apollo Studio</a>
 */
@LinkedSpec(FEDERATION_SPEC)
@GraphQLDirective(
    name = OVERRIDE_DIRECTIVE_NAME,
    description = OVERRIDE_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.FIELD_DEFINITION]
)
annotation class OverrideDirective(val from: String, val label: String = "")

internal const val OVERRIDE_DIRECTIVE_NAME = "override"
internal const val OVERRIDE_DIRECTIVE_FROM_PARAM = "from"
internal const val OVERRIDE_DIRECTIVE_LABEL_PARAM = "label"
private const val OVERRIDE_DIRECTIVE_DESCRIPTION = "Overrides fields resolution logic from other subgraph. Used for migrating fields from one subgraph to another."

/**
 * Creates the override directive definition
 */
internal fun overrideDirectiveDefinition(): graphql.schema.GraphQLDirective {
    val builder = graphql.schema.GraphQLDirective.newDirective()
        .name(OVERRIDE_DIRECTIVE_NAME)
        .description(OVERRIDE_DIRECTIVE_DESCRIPTION)
        .validLocation(DirectiveLocation.FIELD_DEFINITION)
        .argument(
            GraphQLArgument.newArgument()
                .name(OVERRIDE_DIRECTIVE_FROM_PARAM)
                .description("Name of the subgraph to override field resolution")
                .type(GraphQLNonNull(Scalars.GraphQLString))
                .build()
        )

    builder.argument(
        GraphQLArgument.newArgument()
            .name(OVERRIDE_DIRECTIVE_LABEL_PARAM)
            .description("The value must follow the format of 'percent(number)'")
            .type(Scalars.GraphQLString)
            .build()
    )

    return builder.build()
}

/**
 * Converts a GraphQL directive to an applied override directive with proper validation
 * and handling of optional label argument.
 */
internal fun graphql.schema.GraphQLDirective.toAppliedOverrideDirective(directiveInfo: DirectiveMetaInformation): GraphQLAppliedDirective {
    val overrideDirective = directiveInfo.directive as OverrideDirective
    val label = overrideDirective.label.takeIf { it.isNotEmpty() }

    if (!label.isNullOrEmpty() && !validateLabel(label)) {
        throw Exception("@override label must follow the format 'percent(number)', got: $label")
    }

    val builder = GraphQLAppliedDirective.newDirective()
        .name(this.name)
        .argument(GraphQLAppliedDirectiveArgument.newArgument()
            .name(OVERRIDE_DIRECTIVE_FROM_PARAM)
            .type(GraphQLNonNull(Scalars.GraphQLString))
            .valueProgrammatic(overrideDirective.from)
            .build())

    if (!label.isNullOrEmpty()) {
        builder.argument(GraphQLAppliedDirectiveArgument.newArgument()
            .name(OVERRIDE_DIRECTIVE_LABEL_PARAM)
            .type(Scalars.GraphQLString)
            .valueProgrammatic(label)
            .build())
    }

    return builder.build()
}

/**
 * Validates that the label follows the format 'percent(number)'
 * Returns true if the label is valid or null/empty
 */
internal fun validateLabel(label: String?): Boolean {
    if (label.isNullOrEmpty()) return true

    val percentPattern = """^percent\(\d+\)$""".toRegex()
    return percentPattern.matches(label)
}
