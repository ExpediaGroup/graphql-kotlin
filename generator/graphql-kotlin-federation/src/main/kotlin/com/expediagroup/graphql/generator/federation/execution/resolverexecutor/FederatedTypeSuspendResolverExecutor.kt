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

import com.expediagroup.graphql.generator.extensions.getOrDefault
import com.expediagroup.graphql.generator.federation.exception.FederatedRequestFailure
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeSuspendResolver
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.supervisorScope
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.EmptyCoroutineContext

internal object FederatedTypeSuspendResolverExecutor : TypeResolverExecutor<FederatedTypeSuspendResolver<*>> {
    override fun execute(
        resolvableEntities: List<ResolvableEntity<FederatedTypeSuspendResolver<*>>>,
        environment: DataFetchingEnvironment
    ): CompletableFuture<List<Map<Int, Any?>>> =
        resolvableEntities.takeUnless(List<ResolvableEntity<FederatedTypeSuspendResolver<*>>>::isEmpty)
            ?.let {
                environment.graphQlContext.getOrDefault(CoroutineScope(EmptyCoroutineContext)).future {
                    coroutineScope {
                        resolvableEntities.map { resolvableEntity ->
                            async {
                                resolveEntity(resolvableEntity, environment)
                            }
                        }.awaitAll()
                    }
                }
            } ?: CompletableFuture.completedFuture(emptyList())

    @Suppress("TooGenericExceptionCaught")
    private suspend fun resolveEntity(
        resolvableEntity: ResolvableEntity<FederatedTypeSuspendResolver<*>>,
        environment: DataFetchingEnvironment,
    ): Map<Int, Any?> =
        supervisorScope {
            resolvableEntity.indexedRepresentations.associateBy(
                IndexedValue<Map<String, Any>>::index
            ) { (_, representation) ->
                try {
                    resolvableEntity.resolver.resolve(environment, representation)
                } catch (e: Exception) {
                    FederatedRequestFailure(
                        "Exception was thrown while trying to resolve federated type, representation=$representation",
                        e
                    )
                }
            }
        }
}
