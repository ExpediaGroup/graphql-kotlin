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

package com.expediagroup.graphql.generator.directives

import graphql.Directives.DeprecatedDirective
import graphql.schema.GraphQLAppliedDirective

const val DEPRECATED_DIRECTIVE_NAME = "deprecated"

internal fun deprecatedDirectiveWithReason(reason: String): GraphQLAppliedDirective = DeprecatedDirective.toAppliedDirective().transform { directive ->
    val deprecatedArgument = DeprecatedDirective.getArgument("reason").toAppliedArgument().transform { argument ->
        argument.valueProgrammatic(reason)
    }
    directive.argument(deprecatedArgument)
}
