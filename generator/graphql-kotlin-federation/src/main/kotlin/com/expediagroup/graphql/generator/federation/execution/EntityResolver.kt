/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.execution

import graphql.GraphQLError
import graphql.execution.DataFetcherResult
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

private const val TYPENAME_FIELD = "__typename"
private const val REPRESENTATIONS = "representations"

/**
 * Federated _entities query resolver.
 */
interface EntityResolver<T> : DataFetcher<T> {

    /**
     * Pre-computed resolvers by typename, so we don't have to search on every request
     */
    val resolverMap: Map<String, FederatedTypeResolver<*>>

    /**
     * Resolves entities based on the passed in representations argument. Entities are resolved in the same order
     * they are specified in the list of representations. If target representation cannot be resolved, NULL will
     * be returned instead.
     *
     * Representations are grouped by the underlying typename and each batch is resolved asynchronously before merging
     * the results back into a single list that preserves the original order.
     *
     * @return list of resolved nullable entities
     */
    suspend fun resolve(env: DataFetchingEnvironment): DataFetcherResult<List<Any?>> {
        val representations: List<Map<String, Any>> = env.getArgument(REPRESENTATIONS)
        val indexedBatchRequestsByType = representations.withIndex().groupBy { it.value[TYPENAME_FIELD].toString() }

        val data = mutableListOf<Any?>()
        val errors = mutableListOf<GraphQLError>()

        resolveRequests(indexedBatchRequestsByType, env)
            .flatten()
            .sortedBy { it.first }
            .forEach {
                val result = it.second
                if (result is GraphQLError) {
                    data.add(null)
                    errors.add(result)
                } else {
                    data.add(result)
                }
            }

        return DataFetcherResult.newResult<List<Any?>>()
            .data(data)
            .errors(errors)
            .build()
    }

    private suspend fun resolveRequests(indexedBatchRequestsByType: Map<String, List<IndexedValue<Map<String, Any>>>>, env: DataFetchingEnvironment): List<List<Pair<Int, Any?>>> {
        return coroutineScope {
            indexedBatchRequestsByType.map { (typeName, indexedRequests) ->
                async {
                    resolveType(env, typeName, indexedRequests, resolverMap)
                }
            }.awaitAll()
        }
    }
}
