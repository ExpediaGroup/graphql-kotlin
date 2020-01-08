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
import com.expediagroup.graphql.federation.types.FIELD_SET_SCALAR_TYPE
import graphql.introspection.Introspection
import graphql.schema.GraphQLArgument

/**
 * ```graphql
 * directive @key(fields: _FieldSet!) on OBJECT | INTERFACE
 * ```
 *
 * The @key directive is used to indicate a combination of fields that can be used to uniquely identify and fetch an object or interface. Key directive should be specified on the root base type as
 * well as all the corresponding federated (i.e. extended) types. Key fields specified in the directive field set should correspond to a valid field on the underlying GraphQL interface/object.
 * Federated extended types should also instrument all the referenced key fields with @external directive.
 *
 * NOTE: federation spec specifies that multiple @key directives can be applied on the field which is at odds with graphql-spec and currently unsupported by graphql-kotlin.
 *
 * Example:
 * Given
 *
 * ```kotlin
 * @KeyDirective(FieldSet("id"))
 * class Product(val id: String, val name: String)
 * ```
 *
 * should generate
 *
 * ```graphql
 * type Product @key(fields: "id") {
 *   id: String!
 *   name: String!
 * }
 * ```
 *
 * @param fields field set that represents a set of fields forming the key
 *
 * @see FieldSet
 * @see com.expediagroup.graphql.federation.types.FIELD_SET_SCALAR_TYPE
 * @see ExtendsDirective
 * @see ExternalDirective
 */
@GraphQLDirective(
    name = KEY_DIRECTIVE_NAME,
    description = "Space separated list of primary keys needed to access federated object",
    locations = [Introspection.DirectiveLocation.OBJECT, Introspection.DirectiveLocation.INTERFACE]
)
annotation class KeyDirective(val fields: FieldSet)

private const val KEY_DIRECTIVE_NAME = "key"
private const val KEY_DIRECTIVE_ARGUMENT_NAME = "fields"

internal val keyDirectiveType = graphql.schema.GraphQLDirective.newDirective()
    .name(KEY_DIRECTIVE_NAME)
    .argument(GraphQLArgument.newArgument()
        .name(KEY_DIRECTIVE_ARGUMENT_NAME)
        .type(FIELD_SET_SCALAR_TYPE))
    .build()
