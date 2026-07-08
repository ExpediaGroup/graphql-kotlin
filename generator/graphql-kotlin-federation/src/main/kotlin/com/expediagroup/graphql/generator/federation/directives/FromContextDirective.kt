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
import com.expediagroup.graphql.generator.federation.types.fromContextArgumentDefinition
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLScalarType

/**
 * ```graphql
 * directive @fromContext(field: ContextFieldValue) on ARGUMENT_DEFINITION
 * ```
 *
 * The `@fromContext` directive sets the context from which to receive the value of the annotated field.
 * The context must have been defined with the `@context` directive.
 *
 * Example:
 *
 * ```kotlin
 * @KeyDirective(FieldSet("id"))
 * @ContextDirective(name = "userContext")
 * class Product(val id: ID) {
 *   fun discountedPrice(
 *     @FromContextDirective(ContextFieldValue("\$userContext { userId }")) userId: String
 *   ): Int = TODO()
 * }
 * ```
 *
 * @see ContextDirective
 * @see ContextFieldValue
 * @see <a href="https://www.apollographql.com/docs/graphos/schema-design/federated-schemas/reference/directives#fromcontext">@fromContext definition</a>
 */
@LinkedSpec(FEDERATION_SPEC)
@GraphQLDirective(
    name = FROM_CONTEXT_DIRECTIVE_NAME,
    description = FROM_CONTEXT_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.ARGUMENT_DEFINITION]
)
annotation class FromContextDirective(
    /** Selection statement resolving the argument value from a matching `@context`. */
    val field: ContextFieldValue
)

internal const val FROM_CONTEXT_DIRECTIVE_NAME = "fromContext"
private const val FROM_CONTEXT_DIRECTIVE_DESCRIPTION = "Sets the context from which to receive the value of the annotated field"

/**
 * Creates the `@fromContext` directive definition, injecting the (potentially namespaced) `ContextFieldValue` scalar.
 */
internal fun fromContextDirectiveDefinition(contextFieldValueScalar: GraphQLScalarType): graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(FROM_CONTEXT_DIRECTIVE_NAME)
    .description(FROM_CONTEXT_DIRECTIVE_DESCRIPTION)
    .validLocation(DirectiveLocation.ARGUMENT_DEFINITION)
    .argument(fromContextArgumentDefinition(contextFieldValueScalar))
    .build()
