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
import graphql.Scalars
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLArgument

/**
 * ```graphql
 * # federation v1 definition
 * directive @key(fields: _FieldSet!) repeatable on OBJECT | INTERFACE
 *
 * # federation v2 definition
 * directive @key(fields: _FieldSet!, resolvable: Boolean = true) repeatable on OBJECT | INTERFACE
 * ```
 *
 * The `@key` directive is used to indicate a combination of fields that can be used to uniquely identify and fetch an object or interface. The specified field set can represent single field (e.g. `"id"`),
 * multiple fields (e.g. `"id name"`) or nested selection sets (e.g. `"id user { name }"`). Multiple keys can be specified on a target type.
 *
 * Key directives should be specified on all entities (objects that can resolve its fields across multiple subgraphs). Key fields specified in the directive field set should correspond to a valid field
 * on the underlying GraphQL interface/object.
 *
 * Example:
 * Given following entity type definition
 *
 * ```kotlin
 * @KeyDirective(FieldSet("id"))
 * class Product(val id: String, val name: String)
 * ```
 *
 * it will generate following schema
 *
 * ```graphql
 * type Product @key(fields: "id") {
 *   id: String!
 *   name: String!
 * }
 * ```
 *
 * Entity types can be referenced from other subgraphs without contributing any additional fields, i.e. we can update type within our schema with a reference to a federated type. In order to generate
 * a valid schema, we need to define **stub** for federated entity that contains only key fields and also mark it as not resolvable within our subgraph. For example, if we have `Review` entity defined
 * in our supergraph, we can reference it in our product schema using following code
 *
 * ```kotlin
 * @KeyDirective(fields = FieldSet("id"))
 * class Product(val id: String, val name: String, val reviews: List<Review>)
 *
 * // review stub referencing just the key fields
 * @KeyDirective(fields = FieldSet("id"), resolvable = false)
 * class Review(val id: String)
 * ```
 *
 * which will generate
 *
 * ```graphql
 * type Product @key(fields: "id") {
 *   id: String!
 *   name: String!
 *   reviews: [Review!]!
 * }
 *
 * type Review @key(fields: "id", resolvable: false) {
 *   id: String!
 * }
 * ```
 *
 * This allows end users to query GraphQL Gateway for any product review fields and they will be resolved by calling the appropriate subgraph.
 *
 * @param fields field set that represents a set of fields forming the key
 * @param resolvable boolean flag indicating whether this entity can be resolved within this subgraph, only available in Federation v2
 *
 * @see FieldSet
 * @see ExternalDirective
 */
@Repeatable
@GraphQLDirective(
    name = KEY_DIRECTIVE_NAME,
    description = KEY_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.OBJECT, DirectiveLocation.INTERFACE]
)
annotation class KeyDirective(val fields: FieldSet, val resolvable: Boolean = true)

internal const val KEY_DIRECTIVE_NAME = "key"
private const val KEY_DIRECTIVE_DESCRIPTION = "Space separated list of primary keys needed to access federated object"

internal val KEY_DIRECTIVE_TYPE: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(KEY_DIRECTIVE_NAME)
    .description(KEY_DIRECTIVE_DESCRIPTION)
    .validLocations(DirectiveLocation.OBJECT, DirectiveLocation.INTERFACE)
    .argument(FIELD_SET_ARGUMENT)
    .repeatable(true)
    .build()

internal val KEY_DIRECTIVE_TYPE_V2: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(KEY_DIRECTIVE_NAME)
    .description(KEY_DIRECTIVE_DESCRIPTION)
    .validLocations(DirectiveLocation.OBJECT, DirectiveLocation.INTERFACE)
    .argument(FIELD_SET_ARGUMENT)
    .argument(
        GraphQLArgument.newArgument()
            .name("resolvable")
            .type(Scalars.GraphQLBoolean)
            .defaultValueProgrammatic(true)
    )
    .repeatable(true)
    .build()
