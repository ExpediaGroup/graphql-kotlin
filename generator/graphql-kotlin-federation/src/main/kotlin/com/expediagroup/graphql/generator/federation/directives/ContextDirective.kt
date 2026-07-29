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
 * directive @context(name: String!) repeatable on INTERFACE | OBJECT | UNION
 * ```
 *
 * The `@context` directive defines a named context from which a field of the annotated type can be passed
 * to a receiver of the context. The receiver must be a field annotated with the `@fromContext` directive.
 *
 * Example:
 *
 * ```graphql
 * type A @key(fields: "id") @context(name: "userContext") {
 *   id: ID!
 *   prop: String!
 * }
 *
 * type B @key(fields: "id") @context(name: "userContext") {
 *   id: ID!
 *   prop: String!
 * }
 *
 * type U @key(fields: "id") {
 *   id: ID!
 *   field (arg: String @fromContext(field: "$userContext { prop }")): String!
 * }
 * ```
 *
 * @see FromContextDirective
 * @see <a href="https://www.apollographql.com/docs/graphos/schema-design/federated-schemas/reference/directives#context">@context definition</a>
 */
@LinkedSpec(FEDERATION_SPEC)
@Repeatable
@GraphQLDirective(
    name = CONTEXT_DIRECTIVE_NAME,
    description = CONTEXT_DIRECTIVE_DESCRIPTION,
    locations = [
        DirectiveLocation.INTERFACE,
        DirectiveLocation.OBJECT,
        DirectiveLocation.UNION
    ]
)
annotation class ContextDirective(
    /** Unique name identifying the context, referenced by `@fromContext`. */
    val name: String
)

internal const val CONTEXT_DIRECTIVE_NAME = "context"
private const val CONTEXT_DIRECTIVE_DESCRIPTION = "Defines a named context from which a field of the annotated type can be passed to a receiver of the context"
