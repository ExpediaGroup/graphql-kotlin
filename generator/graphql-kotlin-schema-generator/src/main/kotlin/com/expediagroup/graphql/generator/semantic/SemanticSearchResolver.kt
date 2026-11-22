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

package com.expediagroup.graphql.generator.semantic

import graphql.schema.DataFetchingEnvironment

/**
 * Semantic introspection follows the GraphQL AI working group's Semantic Introspection RFC, which is not final:
 * names, arguments and result shape may change in a minor release.
 *
 * See https://github.com/graphql/ai-wg/blob/main/rfcs/semantic-introspection.md
 */
@RequiresOptIn(
    message = "Semantic introspection follows a draft RFC and may change in a minor release.",
    level = RequiresOptIn.Level.WARNING
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class ExperimentalSemanticIntrospection

/**
 * Ranks schema coordinates for a natural language query, the application side of `Query._search`.
 *
 * Read the schema from [DataFetchingEnvironment.getGraphQLSchema] rather than injecting it: the resolver is part of
 * the schema it searches.
 */
@ExperimentalSemanticIntrospection
fun interface SemanticSearchResolver {
    /**
     * Returns at most [SemanticSearchRequest.first] results, best match first, each scoring at least
     * [SemanticSearchRequest.minScore] when set. Only return coordinates of visible schema members: invalid results fail
     * the field instead of being trimmed.
     *
     * Throw a [graphql.GraphQLError] (for example [graphql.GraphqlErrorException]) for an error meant for the client;
     * any other exception goes to the application's [graphql.execution.DataFetcherExceptionHandler].
     */
    suspend fun search(environment: DataFetchingEnvironment, request: SemanticSearchRequest): List<SemanticSearchResult>
}

/**
 * Arguments of `Query._search`.
 */
@ExperimentalSemanticIntrospection
class SemanticSearchRequest(
    /** Natural language description of the wanted capability. */
    val query: String,
    /** Maximum number of results, at least 1. */
    val first: Int,
    /** Minimum score of every returned result. */
    val minScore: Double?
)

/**
 * One `_SearchResult`.
 */
@ExperimentalSemanticIntrospection
class SemanticSearchResult(
    /** Schema coordinate of the match, for example `Type`, `Type.field`, `Type.field(arg:)` or `@directive`. */
    val coordinate: String,
    /** Paths from a root field to the match, each a list of `Type.field` coordinates. */
    val pathsToRoot: List<List<String>>,
    /** Relevance from 0 to 1. */
    val score: Double?
)
