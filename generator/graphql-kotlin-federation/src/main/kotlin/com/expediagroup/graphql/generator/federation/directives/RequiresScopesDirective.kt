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
import com.expediagroup.graphql.generator.directives.DirectiveMetaInformation
import graphql.introspection.Introspection
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLScalarType
import kotlin.reflect.full.memberProperties

/**
 * ```graphql
 * directive @requiresScopes(scopes: [[Scope!]!]!) on
 *     ENUM
 *   | FIELD_DEFINITION
 *   | INTERFACE
 *   | OBJECT
 *   | SCALAR
 * ```
 *
 * Directive that is used to indicate that the target element is accessible only to the authenticated supergraph users with the appropriate JWT scopes. Refer to the
 * <a href="https://www.apollographql.com/docs/router/configuration/authorization#requiresscopes">Apollo Router article</a> for additional details.
 *
 * @see <a href="https://www.apollographql.com/docs/federation/federated-types/federated-directives#requiresscopes">@requiresScope definition</a>
 * @see <a href="https://www.apollographql.com/docs/router/configuration/authorization#requiresscopes">Apollo Router @requiresScope documentation</a>
 */
@LinkedSpec(FEDERATION_SPEC)
@Repeatable
@GraphQLDirective(
    name = REQUIRES_SCOPE_DIRECTIVE_NAME,
    description = REQUIRES_SCOPE_DIRECTIVE_DESCRIPTION,
    locations = [
        Introspection.DirectiveLocation.ENUM,
        Introspection.DirectiveLocation.FIELD_DEFINITION,
        Introspection.DirectiveLocation.INTERFACE,
        Introspection.DirectiveLocation.OBJECT,
        Introspection.DirectiveLocation.SCALAR,
    ]
)
annotation class RequiresScopesDirective(val scopes: Array<Scopes>)

internal const val REQUIRES_SCOPE_DIRECTIVE_NAME = "requiresScopes"
private const val REQUIRES_SCOPE_DIRECTIVE_DESCRIPTION = "Indicates to composition that the target element is accessible only to the authenticated supergraph users with the appropriate JWT scopes"
private const val SCOPES_ARGUMENT = "scopes"

internal fun requiresScopesDirectiveType(scopes: GraphQLScalarType): graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(REQUIRES_SCOPE_DIRECTIVE_NAME)
    .description(REQUIRES_SCOPE_DIRECTIVE_DESCRIPTION)
    .validLocations(
        Introspection.DirectiveLocation.ENUM,
        Introspection.DirectiveLocation.FIELD_DEFINITION,
        Introspection.DirectiveLocation.INTERFACE,
        Introspection.DirectiveLocation.OBJECT,
        Introspection.DirectiveLocation.SCALAR
    )
    .argument(
        GraphQLArgument.newArgument()
            .name("scopes")
            .type(
                GraphQLNonNull.nonNull(
                    GraphQLList.list(
                        GraphQLNonNull(
                            GraphQLList.list(
                                scopes
                            )
                        )
                    )
                )
            )
    )
    .build()

@Suppress("UNCHECKED_CAST")
internal fun graphql.schema.GraphQLDirective.toAppliedRequiresScopesDirective(directiveInfo: DirectiveMetaInformation): GraphQLAppliedDirective {
    // we need to manually transform @requiresScopes directive definition as JVM does not support nested array as annotation arguments
    val annotationScopes = directiveInfo.directive.annotationClass.memberProperties
        .first { it.name == SCOPES_ARGUMENT }
        .call(directiveInfo.directive) as? Array<Scopes> ?: emptyArray()
    val scopes = annotationScopes.map { scopesAnnotation -> scopesAnnotation.value.toList() }

    return this.toAppliedDirective()
        .transform { appliedDirectiveBuilder ->
            this.getArgument(SCOPES_ARGUMENT)
                .toAppliedArgument()
                .transform { argumentBuilder ->
                    argumentBuilder.valueProgrammatic(scopes)
                }
                .let {
                    appliedDirectiveBuilder.argument(it)
                }
        }
}
