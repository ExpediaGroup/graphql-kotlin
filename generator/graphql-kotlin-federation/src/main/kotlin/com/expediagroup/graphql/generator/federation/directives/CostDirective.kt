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
import graphql.introspection.Introspection.DirectiveLocation

/**
 * ```graphql
 * directive @cost(weight: Int!) on
 *     ARGUMENT_DEFINITION
 *   | ENUM
 *   | FIELD_DEFINITION
 *   | INPUT_FIELD_DEFINITION
 *   | OBJECT
 *   | SCALAR
 * ```
 *
 * The `@cost` directive defines a custom weight for a schema location. For GraphOS Router, it customizes the operation cost calculation of the
 * [demand control feature](https://www.apollographql.com/docs/router/executing-operations/demand-control/).
 *
 * If `@cost` is not specified for a field, a default value is used:
 * - Scalars and enums have default cost of 0
 * - Composite input and output types have default cost of 1
 *
 * Regardless of whether `@cost` is specified on a field, the field cost for that field also accounts for its arguments and selections.
 * ```
 *
 * @see <a href="https://www.apollographql.com/docs/router/executing-operations/demand-control/">Apollo Router demand control documentation</a>
 * @see <a href="https://www.apollographql.com/docs/graphos/schema-design/federated-schemas/reference/directives#cost">@cost definition</a>
 */
@LinkedSpec(FEDERATION_SPEC)
@GraphQLDirective(
    name = COST_DIRECTIVE_NAME,
    description = COST_DIRECTIVE_DESCRIPTION,
    locations = [
        DirectiveLocation.ARGUMENT_DEFINITION,
        DirectiveLocation.ENUM,
        DirectiveLocation.FIELD_DEFINITION,
        DirectiveLocation.INPUT_FIELD_DEFINITION,
        DirectiveLocation.OBJECT,
        DirectiveLocation.SCALAR
    ]
)
annotation class CostDirective(
    /** Assigns a custom weight for scoring the current field. */
    val weight: Int,
)

internal const val COST_DIRECTIVE_NAME = "cost"
private const val COST_DIRECTIVE_DESCRIPTION = "Defines a custom weight for a schema location"
