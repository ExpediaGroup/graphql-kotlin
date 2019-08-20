package com.expedia.graphql.federation.execution

import com.expedia.graphql.federation.exception.FederatedRequestFailure
import com.expedia.graphql.federation.exception.FederationException
import com.expedia.graphql.federation.exception.InvalidFederatedRequest
import graphql.GraphQLError
import graphql.execution.DataFetcherResult
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.asCompletableFuture
import java.util.concurrent.CompletableFuture

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
                    val indices = indexedRequests.map { it.index }
                    val batch = indexedRequests.map { it.value }
                    val results = resolveBatch(typeName, batch)
                    if (results.size != indices.size) {
                        throw FederationException("Federation request generated results with different size than requested")
                    }
                    indices.zip(results)
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
