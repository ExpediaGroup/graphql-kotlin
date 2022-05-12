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
 * directive @tag(name: String!) repeatable on FIELD_DEFINITION
 *     | OBJECT
 *     | INTERFACE
 *     | UNION
 *     | ARGUMENT_DEFINITION
 *     | SCALAR
 *     | ENUM
 *     | ENUM_VALUE
 *     | INPUT_OBJECT
 *     | INPUT_FIELD_DEFINITION
 * ```
 *
 * Tag directive allows users to annotate fields and types with additional metadata information.
 *
 * @see <a href="https://www.apollographql.com/docs/studio/contracts/">Apollo Contracts</a>
 * @see <a href="https://specs.apollo.dev/tag/v0.2/">@tag specification</a>
 */
@Repeatable
@GraphQLDirective(
    name = TAG_DIRECTIVE_NAME,
    description = TAG_DIRECTIVE_DESCRIPTION,
    locations = [
        DirectiveLocation.FIELD_DEFINITION,
        DirectiveLocation.OBJECT,
        DirectiveLocation.INTERFACE,
        DirectiveLocation.UNION,
        DirectiveLocation.ARGUMENT_DEFINITION,
        DirectiveLocation.SCALAR,
        DirectiveLocation.ENUM,
        DirectiveLocation.ENUM_VALUE,
        DirectiveLocation.INPUT_OBJECT,
        DirectiveLocation.INPUT_FIELD_DEFINITION
    ]
)
annotation class TagDirective(
    /** Tag value */
    val name: String
)

internal const val TAG_DIRECTIVE_NAME = "tag"
private const val TAG_DIRECTIVE_DESCRIPTION = "Allows users to annotate fields and types with additional metadata information"

internal val TAG_DIRECTIVE_TYPE: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(TAG_DIRECTIVE_NAME)
    .description(TAG_DIRECTIVE_DESCRIPTION)
    .validLocations(
        DirectiveLocation.FIELD_DEFINITION,
        DirectiveLocation.OBJECT,
        DirectiveLocation.INTERFACE,
        DirectiveLocation.UNION,
        DirectiveLocation.ARGUMENT_DEFINITION,
        DirectiveLocation.SCALAR,
        DirectiveLocation.ENUM,
        DirectiveLocation.ENUM_VALUE,
        DirectiveLocation.INPUT_OBJECT,
        DirectiveLocation.INPUT_FIELD_DEFINITION
    )
    .argument(
        GraphQLArgument.newArgument()
            .name("name")
            .type(GraphQLNonNull(Scalars.GraphQLString))
    )
    .repeatable(true)
    .build()
