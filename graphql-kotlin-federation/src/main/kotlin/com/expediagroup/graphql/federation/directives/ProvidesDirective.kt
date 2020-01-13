/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.federation.directives

import com.expediagroup.graphql.annotations.GraphQLDirective
import graphql.introspection.Introspection

/**
 * ```graphql
 * directive @provides(fields: _FieldSet!) on FIELD_DEFINITION
 * ```
 *
 * The @provides directive is used to annotate the expected returned fieldset from a field on a base type that is guaranteed to be selectable by the gateway. This allows you to expose only a subset
 * of fields from the underlying federated object type to be selectable from the federated schema. Provided fields specified in the directive field set should correspond to a valid field on the
 * underlying GraphQL interface/object type. @provides directive can only be used on fields returning federated extended objects.
 *
 * Example:
 * We might want to expose only name of the user that submitted a review.
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
 * @param fields field set that represents a set of fields exposed from the underlying type
 *
 * @see FieldSet
 * @see com.expediagroup.graphql.federation.types.FIELD_SET_SCALAR_TYPE
 * @see ExternalDirective
 * @see ExtendsDirective
 */
@GraphQLDirective(
    name = PROVIDES_DIRECTIVE_NAME,
    description = "Specifies the base type field set that will be selectable by the gateway",
    locations = [Introspection.DirectiveLocation.FIELD_DEFINITION]
)
annotation class ProvidesDirective(val fields: FieldSet)

internal const val PROVIDES_DIRECTIVE_NAME = "provides"
