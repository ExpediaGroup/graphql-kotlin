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

package com.expediagroup.graphql.generator.federation.execution.resolverexecutor

import com.expediagroup.graphql.generator.federation.exception.FederatedRequestFailure
import com.expediagroup.graphql.generator.federation.execution.FederatedTypePromiseResolver
import com.expediagroup.graphql.generator.federation.extensions.collectAll
import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

object FederatedTypePromiseResolverExecutor : TypeResolverExecutor<FederatedTypePromiseResolver<*>> {
    override fun execute(
        resolvableEntities: List<ResolvableEntity<FederatedTypePromiseResolver<*>>>,
        environment: DataFetchingEnvironment
    ): CompletableFuture<List<Map<Int, Any?>>> =
        resolvableEntities.map { resolvableEntity ->
            resolveEntity(resolvableEntity, environment)
        }.collectAll()

    @Suppress("TooGenericExceptionCaught")
    private fun resolveEntity(
        resolvableEntity: ResolvableEntity<FederatedTypePromiseResolver<*>>,
        environment: DataFetchingEnvironment,
    ): CompletableFuture<Map<Int, Any?>> {
        val indexes = resolvableEntity.indexedRepresentations.map(IndexedValue<Map<String, Any>>::index)
        val representations = resolvableEntity.indexedRepresentations.map(IndexedValue<Map<String, Any>>::value)
        val resultsPromise = representations.map { representation ->
            try {
                resolvableEntity.resolver.resolve(environment, representation)
            } catch (e: Exception) {
                CompletableFuture.completedFuture(
                    FederatedRequestFailure(
                        "Exception was thrown while trying to resolve federated type, representation=$representation",
                        e
                    )
                )
            }
        }.collectAll()
        return resultsPromise.thenApply { results ->
            indexes.zip(results).toMap()
        }
    }
}
