/*
 * Copyright 2026 Expedia, Inc
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
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull

/**
 * ```graphql
 * directive @listSize(
 *   assumedSize: Int,
 *   slicingArguments: [String!],
 *   sizedFields: [String!],
 *   requireOneSlicingArgument: Boolean = true
 * ) on FIELD_DEFINITION
 * ```
 *
 * The `@listSize` directive is used to customize the cost calculation of the
 * [demand control feature](https://www.apollographql.com/docs/router/executing-operations/demand-control/)
 * of GraphOS Router.
 *
 * In the static analysis phase, the cost calculator does not know how many entities
 * will be returned by each list field in a given query.
 *
 * By providing an estimated list size for a field with `@listSize`, the cost
 * calculator can produce a more accurate estimate of the cost during static analysis.
 *
 * @see <a href="https://www.apollographql.com/docs/router/executing-operations/demand-control/">Apollo Router demand control documentation</a>
 * @see <a href="https://www.apollographql.com/docs/graphos/schema-design/federated-schemas/reference/directives#listsize">@listSize definition</a>
 */
@LinkedSpec(FEDERATION_SPEC)
@GraphQLDirective(
    name = LIST_SIZE_DIRECTIVE_NAME,
    description = LIST_SIZE_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.FIELD_DEFINITION]
)
annotation class ListSizeDirective(
    /** Indicates that the annotated list field will return at most this many items. */
    val assumedSize: Int = -1,

    /**
     * Indicates that the annotated list field returns as many items as are requested by a paging argument.
     * If multiple arguments are passed, the maximum value of the arguments is used. If both this and [assumedSize]
     * are specified, the value from [slicingArguments] will take precedence.
     */
    val slicingArguments: Array<String> = [],

    /** Supports cursor objects by indicating that the expected list size should be applied to fields within the returned object. */
    val sizedFields: Array<String> = [],

    /**
     * If `true`, indicates that queries must supply exactly one argument from slicingArguments.
     * If [slicingArguments] are not specified, this value is ignored. The default value is `true`.
     */
    val requireOneSlicingArgument: Boolean = true,
)

internal const val LIST_SIZE_DIRECTIVE_NAME = "listSize"
internal const val LIST_SIZE_DIRECTIVE_ASSUMED_SIZE_PARAM = "assumedSize"
internal const val LIST_SIZE_DIRECTIVE_SLICING_ARGUMENTS_PARAM = "slicingArguments"
internal const val LIST_SIZE_DIRECTIVE_SIZED_FIELDS_PARAM = "sizedFields"
internal const val LIST_SIZE_DIRECTIVE_REQUIRE_ONE_SLICING_ARGUMENT_PARAM = "requireOneSlicingArgument"
private const val LIST_SIZE_DIRECTIVE_DESCRIPTION = "Used to customize the cost calculation of a list field as used by demand control features"

/**
 * Creates the `@listSize` directive definition, matching the Federation specification where all arguments are optional.
 */
internal fun listSizeDirectiveDefinition(): graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(LIST_SIZE_DIRECTIVE_NAME)
    .description(LIST_SIZE_DIRECTIVE_DESCRIPTION)
    .validLocation(DirectiveLocation.FIELD_DEFINITION)
    .argument(
        GraphQLArgument.newArgument()
            .name(LIST_SIZE_DIRECTIVE_ASSUMED_SIZE_PARAM)
            .type(Scalars.GraphQLInt)
    )
    .argument(
        GraphQLArgument.newArgument()
            .name(LIST_SIZE_DIRECTIVE_SLICING_ARGUMENTS_PARAM)
            .type(GraphQLList.list(GraphQLNonNull(Scalars.GraphQLString)))
    )
    .argument(
        GraphQLArgument.newArgument()
            .name(LIST_SIZE_DIRECTIVE_SIZED_FIELDS_PARAM)
            .type(GraphQLList.list(GraphQLNonNull(Scalars.GraphQLString)))
    )
    .argument(
        GraphQLArgument.newArgument()
            .name(LIST_SIZE_DIRECTIVE_REQUIRE_ONE_SLICING_ARGUMENT_PARAM)
            .type(Scalars.GraphQLBoolean)
            .defaultValueProgrammatic(true)
    )
    .build()

/**
 * Converts a `@listSize` GraphQL directive to an applied directive, only emitting the arguments that were explicitly
 * provided (i.e. differ from their "unspecified" defaults).
 */
internal fun graphql.schema.GraphQLDirective.toAppliedListSizeDirective(directiveInfo: DirectiveMetaInformation): GraphQLAppliedDirective {
    val listSizeDirective = directiveInfo.directive as ListSizeDirective

    val builder = GraphQLAppliedDirective.newDirective().name(this.name)

    if (listSizeDirective.assumedSize >= 0) {
        builder.argument(
            GraphQLAppliedDirectiveArgument.newArgument()
                .name(LIST_SIZE_DIRECTIVE_ASSUMED_SIZE_PARAM)
                .type(Scalars.GraphQLInt)
                .valueProgrammatic(listSizeDirective.assumedSize)
                .build()
        )
    }

    if (listSizeDirective.slicingArguments.isNotEmpty()) {
        builder.argument(
            GraphQLAppliedDirectiveArgument.newArgument()
                .name(LIST_SIZE_DIRECTIVE_SLICING_ARGUMENTS_PARAM)
                .type(GraphQLList.list(GraphQLNonNull(Scalars.GraphQLString)))
                .valueProgrammatic(listSizeDirective.slicingArguments.toList())
                .build()
        )
    }

    if (listSizeDirective.sizedFields.isNotEmpty()) {
        builder.argument(
            GraphQLAppliedDirectiveArgument.newArgument()
                .name(LIST_SIZE_DIRECTIVE_SIZED_FIELDS_PARAM)
                .type(GraphQLList.list(GraphQLNonNull(Scalars.GraphQLString)))
                .valueProgrammatic(listSizeDirective.sizedFields.toList())
                .build()
        )
    }

    // only emit when it differs from the spec default (true)
    if (!listSizeDirective.requireOneSlicingArgument) {
        builder.argument(
            GraphQLAppliedDirectiveArgument.newArgument()
                .name(LIST_SIZE_DIRECTIVE_REQUIRE_ONE_SLICING_ARGUMENT_PARAM)
                .type(Scalars.GraphQLBoolean)
                .valueProgrammatic(false)
                .build()
        )
    }

    return builder.build()
}
