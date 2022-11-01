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
import com.expediagroup.graphql.generator.federation.types.FIELD_SET_ARGUMENT
import graphql.introspection.Introspection.DirectiveLocation

/**
 * ```graphql
 * directive @provides(fields: _FieldSet!) on FIELD_DEFINITION
 * ```
 *
 * The `@provides` directive is a router optimization hint specifying field set that can be resolved locally at the given subgraph through this particular query path. This allows you to expose only a
 * subset of fields from the underlying entity type to be selectable from the federated schema without the need to call other subgraphs. Provided fields specified in the directive field set should
 * correspond to a valid field on the underlying GraphQL interface/object type. `@provides` directive can only be used on fields returning entities.
 *
 * >NOTE: Federation v2 does not require `@provides` directive if field can **always** be resolved locally. `@provides` should be omitted in this situation.
 *
 * Example 1:
 * We might want to expose only name of the user that submitted a review.
 *
 *
 * ```kotlin
 * @KeyDirective(FieldSet("id"))
 * class Review(val id: String) {
 *   @ProvidesDirective(FieldSet("name"))
 *   fun user(): User = // implementation goes here
 * }
 *
 * @KeyDirective(FieldSet("userId"))
 * @ExtendsDirective
 * class User(
 *   @ExternalDirective val userId: String,
 *   @ExternalDirective val name: String
 * )
 * ```
 *
 * should generate
 *
 * ```graphql
 * type Review @key(fields : "id") {
 *   id: String!
 *   user: User! @provides(fields : "name")
 * }
 *
 * type User @extends @key(fields : "userId") {
 *   userId: String! @external
 *   name: String! @external
 * }
 * ```
 *
 * Example 2:
 * Within our service, one of the queries could resolve all fields locally while other requires resolution from other subgraph
 *
 * ```graphql
 * type Query {
 *   remoteResolution: Foo
 *   localOnly: Foo @provides("baz")
 * }
 *
 * type Foo @key("id") {
 *   id: ID!
 *   bar: Bar
 *   baz: Baz @external
 * }
 * ```
 *
 * In the example above, if user selects `baz` field, it will be resolved locally from `localOnly` query but will require another subgraph invocation from `remoteResolution` query.
 *
 * @param fields field set that represents a set of fields exposed from the underlying type
 *
 * @see FieldSet
 * @see ExternalDirective
 */
@GraphQLDirective(
    name = PROVIDES_DIRECTIVE_NAME,
    description = PROVIDES_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.FIELD_DEFINITION]
)
annotation class ProvidesDirective(val fields: FieldSet)

internal const val PROVIDES_DIRECTIVE_NAME = "provides"
private const val PROVIDES_DIRECTIVE_DESCRIPTION = "Specifies the base type field set that will be selectable by the gateway"

internal val PROVIDES_DIRECTIVE_TYPE: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(PROVIDES_DIRECTIVE_NAME)
    .description(PROVIDES_DIRECTIVE_DESCRIPTION)
    .validLocations(DirectiveLocation.FIELD_DEFINITION)
    .argument(FIELD_SET_ARGUMENT)
    .build()
