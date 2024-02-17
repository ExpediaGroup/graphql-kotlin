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
import graphql.introspection.Introspection

/**
 * ```graphql
 * directive @authenticated on
 *     ENUM
 *   | FIELD_DEFINITION
 *   | INTERFACE
 *   | OBJECT
 *   | SCALAR
 * ```
 *
 * Directive that is used to indicate that the target element is accessible only to the authenticated supergraph users. For more granular access control, see the @requiresScopes directive usage.
 * Refer to the <a href="https://www.apollographql.com/docs/router/configuration/authorization#authenticated">Apollo Router article</a> for additional details.
 *
 * @see <a href="https://www.apollographql.com/docs/federation/federated-types/federated-directives#authenticated">@authenticated definition</a>
 * @see <a href="https://www.apollographql.com/docs/router/configuration/authorization#authenticated">Apollo Router @authenticated documentation</a>
 */
@LinkedSpec(FEDERATION_SPEC)
@Repeatable
@GraphQLDirective(
    name = AUTHENTICATED_DIRECTIVE_NAME,
    description = AUTHENTICATED_DIRECTIVE_DESCRIPTION,
    locations = [
        Introspection.DirectiveLocation.ENUM,
        Introspection.DirectiveLocation.FIELD_DEFINITION,
        Introspection.DirectiveLocation.INTERFACE,
        Introspection.DirectiveLocation.OBJECT,
        Introspection.DirectiveLocation.SCALAR,
    ]
)
annotation class AuthenticatedDirective

internal const val AUTHENTICATED_DIRECTIVE_NAME = "authenticated"
private const val AUTHENTICATED_DIRECTIVE_DESCRIPTION = "Indicates to composition that the target element is accessible only to the authenticated supergraph users"
