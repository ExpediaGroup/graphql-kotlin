/*
 * Copyright 2019 Expedia Group, Inc.
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
