package com.expediagroup.graphql.federation.execution

import com.expediagroup.graphql.federation.exception.FederatedRequestFailure
import com.expediagroup.graphql.federation.exception.InvalidFederatedRequest
import graphql.GraphQLError
import graphql.execution.DataFetcherResult
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.asCompletableFuture
import java.util.concurrent.CompletableFuture

/**
 * Federated _entities query resolver.
 */
open class EntityResolver(private val federatedTypeRegistry: FederatedTypeRegistry) : DataFetcher<CompletableFuture<DataFetcherResult<List<Any?>>>> {

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
        val representations: List<Map<String, Any>> = env.getArgument("representations")

        val indexedBatchRequestsByType = representations.withIndex().groupBy { it.value["__typename"].toString() }
        return GlobalScope.async {
            val data = mutableListOf<Any?>()
            val errors = mutableListOf<GraphQLError>()
            indexedBatchRequestsByType.map { (typeName, indexedRequests) ->
                async {
                    resolveType(typeName, indexedRequests)
                }
            }.awaitAll()
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
            DataFetcherResult.newResult<List<Any?>>()
                .data(data)
                .errors(errors)
                .build()
        }.asCompletableFuture()
    }

    private suspend fun resolveType(typeName: String, indexedRequests: List<IndexedValue<Map<String, Any>>>): List<Pair<Int, Any?>> {
        val indices = indexedRequests.map { it.index }
        val batch = indexedRequests.map { it.value }
        val results = resolveBatch(typeName, batch)
        return if (results.size != indices.size) {
            indices.map {
                it to FederatedRequestFailure("Federation batch request for $typeName generated different number of results than requested, representations=${indices.size}, results=${results.size}")
            }
        } else {
            indices.zip(results)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun resolveBatch(typeName: String, batch: List<Map<String, Any>>): List<Any?> {
        val resolver = federatedTypeRegistry.getFederatedResolver(typeName)
        return if (resolver != null) {
            try {
                resolver.resolve(batch)
            } catch (e: Exception) {
                batch.map {
                    FederatedRequestFailure("Exception was thrown while trying to resolve federated type, representation=$it", e)
                }
            }
        } else {
            batch.map {
                InvalidFederatedRequest("Unable to resolve federated type, representation=$it")
            }
        }
    }
}
