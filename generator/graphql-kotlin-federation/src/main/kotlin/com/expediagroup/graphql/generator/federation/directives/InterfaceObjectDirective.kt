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
 * directive @interfaceObject on OBJECT
 * ```
 *
 * This directive provides meta information to the router that this entity type defined within this subgraph is an interface in the supergraph. This allows you to extend functionality
 * of an interface across the supergraph without having to implement (or even be aware of) all its implementing types.
 *
 * Example:
 * Given an interface that is defined in another subgraph
 *
 * ```graphql
 * interface Product @key(fields: "id") {
 *   id: ID!
 *   description: String
 * }
 *
 * type Book implements Product @key(fields: "id") {
 *   id: ID!
 *   description: String
 *   pages: Int!
 * }
 *
 * type Movie implements Product @key(fields: "id") {
 *   id: ID!
 *   description: String
 *   duration: Int!
 * }
 * ```
 *
 * We can extend Product entity in our subgraph and a new field directly to it. This will result in making this new field available to ALL implementing types.
 *
 * ```kotlin
 * @InterfaceObjectDirective
 * data class Product(val id: ID) {
 *     fun reviews(): List<Review> = TODO()
 * }
 * ```
 *
 * Which generates the following subgraph schema
 *
 * ```graphql
 * type Product @key(fields: "id") @interfaceObject {
 *   id: ID!
 *   reviews: [Review!]!
 * }
 * ```
 */
@GraphQLDirective(
    name = INTERFACE_OBJECT_DIRECTIVE_NAME,
    description = INTERFACE_OBJECT_DIRECTIVE_DESCRIPTION,
    locations = [Introspection.DirectiveLocation.OBJECT]
)
annotation class InterfaceObjectDirective

internal const val INTERFACE_OBJECT_DIRECTIVE_NAME = "interfaceObject"
private const val INTERFACE_OBJECT_DIRECTIVE_DESCRIPTION = "Provides meta information to the router that this entity type is an interface in the supergraph."

internal val INTERFACE_OBJECT_DIRECTIVE_TYPE: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(INTERFACE_OBJECT_DIRECTIVE_NAME)
    .description(INTERFACE_OBJECT_DIRECTIVE_DESCRIPTION)
    .validLocations(Introspection.DirectiveLocation.OBJECT)
    .build()
