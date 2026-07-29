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
 * directive @cacheTag(format: String!) repeatable on FIELD_DEFINITION | OBJECT
 * ```
 *
 * Assigns cache tags to cached data in the Apollo Router for [active cache invalidation](https://www.apollographql.com/docs/graphos/routing/performance/caching/response-caching/invalidation#active-invalidation).
 * Use cache tags to remove specific cached entries on demand when data changes, instead of waiting for time-to-live (TTL) expiration.
 *
 * Example:
 *
 * ```graphql
 * extend schema
 *   @link(url: "https://specs.apollo.dev/federation/v2.12",
 *         import: ["@key", "@cacheTag"])
 *
 * type Query {
 *   users: [User!]! @cacheTag(format: "users-list")
 *   user(id: ID!): User @cacheTag(format: "user-{$args.id}")
 * }
 *
 * type User @key(fields: "id") @cacheTag(format: "user-{$key.id}") {
 *   id: ID!
 *   name: String!
 * }
 * ```
 *
 * @see <a href="https://www.apollographql.com/docs/graphos/routing/performance/caching/response-caching/overview">Apollo Router response caching documentation</a>
 * @see <a href="https://www.apollographql.com/docs/graphos/schema-design/federated-schemas/reference/directives#cachetag">@cacheTag definition</a>
 */
@LinkedSpec(FEDERATION_SPEC)
@Repeatable
@GraphQLDirective(
    name = CACHE_TAG_DIRECTIVE_NAME,
    description = CACHE_TAG_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.FIELD_DEFINITION, DirectiveLocation.OBJECT]
)
annotation class CacheTagDirective(
  /**
   * A string template that defines the cache tag.
   *
   * Can include interpolated variables:
   * - For root fields: `{$args.fieldName}` to interpolate field arguments
   * - For entities: `{$key.fieldName}` to interpolate entity key fields
   * Interpolated variables must be either a scalar or enum type.
   */
  val format: String,
)

internal const val CACHE_TAG_DIRECTIVE_NAME = "cacheTag"
private const val CACHE_TAG_DIRECTIVE_DESCRIPTION = "Assigns cache tags to cached data in the Apollo Router for active cache invalidation"
