/*
 * Copyright 2024 Expedia, Inc
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
 * directive @policy(scopes: [[Policy!]!]!) on
 *     ENUM
 *   | FIELD_DEFINITION
 *   | INTERFACE
 *   | OBJECT
 *   | SCALAR
 * ```
 *
 *
 * Directive that is used to indicate that the target element is restricted based on authorization policies that are evaluated in a Rhai script or coprocessor.
 * Refer to the <a href="https://www.apollographql.com/docs/router/configuration/authorization#policy">Apollo Router article</a> for additional details.
 *
 * @see <a href="https://www.apollographql.com/docs/federation/federated-types/federated-directives#policy">@policy definition</a>
 * @see <a href="https://www.apollographql.com/docs/router/configuration/authorization#policy">Apollo Router @policy documentation</a>
 */
@LinkedSpec(FEDERATION_SPEC)
@Repeatable
@GraphQLDirective(
    name = POLICY_DIRECTIVE_NAME,
    description = POLICY_DIRECTIVE_DESCRIPTION,
    locations = [
        Introspection.DirectiveLocation.ENUM,
        Introspection.DirectiveLocation.FIELD_DEFINITION,
        Introspection.DirectiveLocation.INTERFACE,
        Introspection.DirectiveLocation.OBJECT,
        Introspection.DirectiveLocation.SCALAR,
    ]
)
annotation class PolicyDirective(val policies: Array<Policies>)

internal const val POLICY_DIRECTIVE_NAME = "policy"
private const val POLICY_DIRECTIVE_DESCRIPTION = "Indicates to composition that the target element is restricted based on authorization policies that are evaluated in a Rhai script or coprocessor"
private const val POLICIES_ARGUMENT = "policies"

internal fun policyDirectiveDefinition(policies: GraphQLScalarType): graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(POLICY_DIRECTIVE_NAME)
    .description(POLICY_DIRECTIVE_DESCRIPTION)
    .validLocations(
        Introspection.DirectiveLocation.ENUM,
        Introspection.DirectiveLocation.FIELD_DEFINITION,
        Introspection.DirectiveLocation.INTERFACE,
        Introspection.DirectiveLocation.OBJECT,
        Introspection.DirectiveLocation.SCALAR
    )
    .argument(
        GraphQLArgument.newArgument()
            .name(POLICIES_ARGUMENT)
            .type(
                GraphQLNonNull.nonNull(
                    GraphQLList.list(
                        GraphQLNonNull(
                            GraphQLList.list(
                                policies
                            )
                        )
                    )
                )
            )
    )
    .build()

@Suppress("UNCHECKED_CAST")
internal fun graphql.schema.GraphQLDirective.toAppliedPolicyDirective(directiveInfo: DirectiveMetaInformation): GraphQLAppliedDirective {
    // we need to manually transform @policy directive definition as JVM does not support nested array as annotation arguments
    val annotationPolicies = directiveInfo.directive.annotationClass.memberProperties
        .first { it.name == POLICIES_ARGUMENT }
        .call(directiveInfo.directive) as? Array<Policies> ?: emptyArray()
    val policies = annotationPolicies.map { policiesAnnotation -> policiesAnnotation.value.toList() }

    return this.toAppliedDirective()
        .transform { appliedDirectiveBuilder ->
            this.getArgument(POLICIES_ARGUMENT)
                .toAppliedArgument()
                .transform { argumentBuilder ->
                    argumentBuilder.valueProgrammatic(policies)
                }
                .let {
                    appliedDirectiveBuilder.argument(it)
                }
        }
}
