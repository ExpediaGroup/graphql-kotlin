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
import com.expediagroup.graphql.generator.directives.DirectiveMetaInformation
import graphql.Scalars
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLAppliedDirectiveArgument
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLEnumType

/**
 * ```graphql
 * enum CacheControlScope {
 *   PUBLIC
 *   PRIVATE
 * }
 *
 * directive @cacheControl(
 *   maxAge: Int
 *   scope: CacheControlScope
 *   inheritMaxAge: Boolean
 * ) on FIELD_DEFINITION | OBJECT | INTERFACE | UNION
 * ```
 *
 * Static cache hints for the [Apollo cache control](https://www.apollographql.com/docs/apollo-server/performance/caching)
 * model. Hints are applied to types and fields and are combined per operation into a single cache policy: the lowest
 * `maxAge` wins and a single `PRIVATE` hint makes the whole response private.
 *
 * The directive itself only annotates the schema. The policy is calculated at execution time by the
 * `com.apollographql.federation.graphqljava.caching.CacheControlInstrumentation` from `federation-jvm`. The
 * `graphql-kotlin-spring-server` registers that instrumentation and writes the resulting `Cache-Control` response header
 * when `graphql.federation.cacheControl.enabled=true`, which is what the Apollo Router and CDNs use to cache subgraph
 * responses.
 *
 * Unlike the federation spec directives, `@cacheControl` does not need to be imported through `@link`.
 *
 * Example:
 *
 * ```kotlin
 * @CacheControlDirective(maxAge = 240)
 * data class Product(val id: ID, val name: String) {
 *   @CacheControlDirective(maxAge = 30)
 *   fun price(): Double = TODO()
 *
 *   @CacheControlDirective(scope = CacheControlScope.PRIVATE)
 *   fun recommendedForYou(): List<Product> = TODO()
 * }
 * ```
 *
 * will generate
 *
 * ```graphql
 * type Product @cacheControl(maxAge : 240) {
 *   id: ID!
 *   name: String!
 *   price: Float! @cacheControl(maxAge : 30)
 *   recommendedForYou: [Product!]! @cacheControl(scope : PRIVATE)
 * }
 * ```
 *
 * @see <a href="https://www.apollographql.com/docs/apollo-server/performance/caching">Apollo Server caching documentation</a>
 * @see <a href="https://www.apollographql.com/docs/graphos/routing/performance/caching/response-caching/overview">Apollo Router response caching documentation</a>
 */
@GraphQLDirective(
    name = CACHE_CONTROL_DIRECTIVE_NAME,
    description = CACHE_CONTROL_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.FIELD_DEFINITION, DirectiveLocation.OBJECT, DirectiveLocation.INTERFACE, DirectiveLocation.UNION]
)
annotation class CacheControlDirective(
    /**
     * Maximum amount of time, in seconds, that the result of this field or type can be cached. Negative values (the
     * default) mean that no `maxAge` argument is emitted and the default cache policy applies.
     */
    val maxAge: Int = -1,
    /**
     * Whether the cached value is shared by all users ([CacheControlScope.PUBLIC]) or specific to a single user
     * ([CacheControlScope.PRIVATE]). Defaults to [CacheControlScope.UNSET], meaning that no `scope` argument is emitted.
     */
    val scope: CacheControlScope = CacheControlScope.UNSET,
    /**
     * If `true`, the field inherits the `maxAge` of its parent field instead of using the default `maxAge`.
     * Only meaningful on fields that return a composite type and do not specify their own [maxAge].
     */
    val inheritMaxAge: Boolean = false
)

/**
 * Scope of a [CacheControlDirective] hint.
 */
enum class CacheControlScope {
    /** Cached value can be shared across users. */
    PUBLIC,

    /** Cached value is specific to a single user. */
    PRIVATE,

    /** No scope hint, the `scope` argument is omitted from the generated directive. */
    UNSET
}

internal const val CACHE_CONTROL_DIRECTIVE_NAME = "cacheControl"
internal const val CACHE_CONTROL_SCOPE_TYPE_NAME = "CacheControlScope"
private const val CACHE_CONTROL_DIRECTIVE_DESCRIPTION = "Provides static cache hints that are used to compute the Cache-Control policy of a response"
private const val CACHE_CONTROL_DIRECTIVE_MAX_AGE_PARAM = "maxAge"
private const val CACHE_CONTROL_DIRECTIVE_SCOPE_PARAM = "scope"
private const val CACHE_CONTROL_DIRECTIVE_INHERIT_MAX_AGE_PARAM = "inheritMaxAge"

/**
 * `CacheControlScope` enum used by the `@cacheControl` directive. Enum values are backed by their names, which is the
 * representation the `federation-jvm` `CacheControlInstrumentation` expects when it reads the applied directive.
 */
internal val CACHE_CONTROL_SCOPE_TYPE: GraphQLEnumType = GraphQLEnumType.newEnum()
    .name(CACHE_CONTROL_SCOPE_TYPE_NAME)
    .value(CacheControlScope.PUBLIC.name)
    .value(CacheControlScope.PRIVATE.name)
    .build()

/**
 * Programmatic `@cacheControl` directive definition.
 */
internal fun cacheControlDirectiveDefinition(): graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(CACHE_CONTROL_DIRECTIVE_NAME)
    .description(CACHE_CONTROL_DIRECTIVE_DESCRIPTION)
    .validLocations(DirectiveLocation.FIELD_DEFINITION, DirectiveLocation.OBJECT, DirectiveLocation.INTERFACE, DirectiveLocation.UNION)
    .argument(
        GraphQLArgument.newArgument()
            .name(CACHE_CONTROL_DIRECTIVE_MAX_AGE_PARAM)
            .type(Scalars.GraphQLInt)
    )
    .argument(
        GraphQLArgument.newArgument()
            .name(CACHE_CONTROL_DIRECTIVE_SCOPE_PARAM)
            .type(CACHE_CONTROL_SCOPE_TYPE)
    )
    .argument(
        GraphQLArgument.newArgument()
            .name(CACHE_CONTROL_DIRECTIVE_INHERIT_MAX_AGE_PARAM)
            .type(Scalars.GraphQLBoolean)
    )
    .build()

/**
 * Applies `@cacheControl` emitting only the arguments that were explicitly set on the annotation, so that unset hints
 * do not override hints inherited from the return type (e.g. a field level `maxAge` hint should not reset the type scope).
 */
internal fun graphql.schema.GraphQLDirective.toAppliedCacheControlDirective(directiveInfo: DirectiveMetaInformation): GraphQLAppliedDirective {
    val cacheControlDirective = directiveInfo.directive as CacheControlDirective
    val builder = GraphQLAppliedDirective.newDirective().name(this.name)

    if (cacheControlDirective.maxAge >= 0) {
        builder.argument(
            GraphQLAppliedDirectiveArgument.newArgument()
                .name(CACHE_CONTROL_DIRECTIVE_MAX_AGE_PARAM)
                .type(Scalars.GraphQLInt)
                .valueProgrammatic(cacheControlDirective.maxAge)
                .build()
        )
    }

    if (cacheControlDirective.scope != CacheControlScope.UNSET) {
        builder.argument(
            GraphQLAppliedDirectiveArgument.newArgument()
                .name(CACHE_CONTROL_DIRECTIVE_SCOPE_PARAM)
                .type(CACHE_CONTROL_SCOPE_TYPE)
                .valueProgrammatic(cacheControlDirective.scope.name)
                .build()
        )
    }

    if (cacheControlDirective.inheritMaxAge) {
        builder.argument(
            GraphQLAppliedDirectiveArgument.newArgument()
                .name(CACHE_CONTROL_DIRECTIVE_INHERIT_MAX_AGE_PARAM)
                .type(Scalars.GraphQLBoolean)
                .valueProgrammatic(true)
                .build()
        )
    }

    return builder.build()
}
