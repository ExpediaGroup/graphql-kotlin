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

package com.expediagroup.graphql.generator.federation.execution

import com.expediagroup.graphql.generator.federation.exception.InvalidFederatedRequest
import com.expediagroup.graphql.generator.federation.execution.resolverexecutor.FederatedTypePromiseResolverExecutor
import com.expediagroup.graphql.generator.federation.execution.resolverexecutor.FederatedTypeSuspendResolverExecutor
import com.expediagroup.graphql.generator.federation.execution.resolverexecutor.ResolvableEntity
import com.expediagroup.graphql.generator.federation.extensions.collectAll
import com.expediagroup.graphql.generator.federation.extensions.toDataFetcherResult
import graphql.execution.DataFetcherResult
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

private const val TYPENAME_FIELD = "__typename"
private const val REPRESENTATIONS = "representations"

/**
 * Federated _entities field data fetcher.
 */
open class EntitiesDataFetcher(
    resolvers: List<FederatedTypeResolver>
) : DataFetcher<CompletableFuture<DataFetcherResult<List<Any?>>>> {

    constructor(vararg resolvers: FederatedTypeResolver) : this(resolvers.toList())
    /**
     * Pre-compute resolvers by typename so, we don't have to search on every request
     */
    private val resolversByType: Map<String, FederatedTypeResolver> = resolvers.associateBy(FederatedTypeResolver::typeName)

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
    override fun get(env: DataFetchingEnvironment): CompletableFuture<DataFetcherResult<List<Any?>>> {
        val representations: List<Map<String, Any>> = env.getArgument(REPRESENTATIONS)

        val representationsWithoutResolver = mutableListOf<IndexedValue<Map<String, Any>>>()
        val entitiesWithPromiseResolver = mutableListOf<ResolvableEntity<FederatedTypePromiseResolver<*>>>()
        val entitiesWithSuspendResolver = mutableListOf<ResolvableEntity<FederatedTypeSuspendResolver<*>>>()

        representations.withIndex()
            .groupBy { (_, representation) -> representation[TYPENAME_FIELD].toString() }
            .forEach { (typeName, indexedRequests) ->
                when (val resolver = resolversByType[typeName]) {
                    is FederatedTypePromiseResolver<*> -> {
                        entitiesWithPromiseResolver += ResolvableEntity(typeName, indexedRequests, resolver)
                    }
                    is FederatedTypeSuspendResolver<*> -> {
                        entitiesWithSuspendResolver += ResolvableEntity(typeName, indexedRequests, resolver)
                    }
                    null -> {
                        representationsWithoutResolver += indexedRequests
                    }
                }
            }

        val noResolverErrors: CompletableFuture<List<Map<Int, Any?>>> = CompletableFuture.completedFuture(
            listOf(
                representationsWithoutResolver.associateBy(IndexedValue<Map<String, Any>>::index) { (_, representation) ->
                    InvalidFederatedRequest("Unable to resolve federated type, representation=$representation")
                }
            )
        )

        val promises: List<CompletableFuture<List<Map<Int, Any?>>>> = listOf(
            FederatedTypePromiseResolverExecutor.execute(entitiesWithPromiseResolver, env),
            FederatedTypeSuspendResolverExecutor.execute(entitiesWithSuspendResolver, env),
            noResolverErrors
        )

        return promises
            .collectAll()
            .thenApply { results ->
                results.asSequence()
                    .flatten()
                    .map(Map<Int, Any?>::toList)
                    .flatten()
                    .sortedBy(Pair<Int, Any?>::first)
                    .map(Pair<Int, Any?>::second)
                    .toDataFetcherResult()
            }
    }
}
