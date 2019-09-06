package com.expediagroup.graphql.federation.execution

import com.expediagroup.graphql.federation.exception.FederatedRequestFailure
import com.expediagroup.graphql.federation.exception.InvalidFederatedRequest

internal suspend fun resolveType(typeName: String, indexedRequests: List<IndexedValue<Map<String, Any>>>, federatedTypeRegistry: FederatedTypeRegistry): List<Pair<Int, Any?>> {
    val indices = indexedRequests.map { it.index }
    val batch = indexedRequests.map { it.value }
    val results = resolveBatch(typeName, batch, federatedTypeRegistry)
    return if (results.size != indices.size) {
        indices.map {
            it to FederatedRequestFailure("Federation batch request for $typeName generated different number of results than requested, representations=${indices.size}, results=${results.size}")
        }
    } else {
        indices.zip(results)
    }
}

@Suppress("TooGenericExceptionCaught")
private suspend fun resolveBatch(typeName: String, batch: List<Map<String, Any>>, federatedTypeRegistry: FederatedTypeRegistry): List<Any?> {
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
