/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.dataloader.instrumentation.level.state

import graphql.execution.Async
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.LightDataFetcher
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

/**
 * LightDataFetcher Decorator that stores the original dataFetcher result (it's always a completable future)
 * it stores the [originalFuture] as property and returns an uncompleted [manualFuture]
 * then at later point manually call [complete] to complete the [manualFuture] with the [originalFuture] result
 * to let ExecutionStrategy handle all futures
 *
 * @param originalDataFetcher original dataFetcher to be decorated
 */
class ManuallyCompletableLightDataFetcher(
    private val originalDataFetcher: LightDataFetcher<*>
) : ManualDataFetcher(), LightDataFetcher<CompletableFuture<Any?>> {

    override fun get(environment: DataFetchingEnvironment): CompletableFuture<Any?> =
        get(environment.fieldDefinition, environment.getSource()) { environment }

    /**
     * when attempting to get the value from LightDataFetcher, execute the [originalDataFetcher]
     * and store the resulting future [originalFuture] and a possible [originalExpressionException] if
     * a synchronous exception was thrown during the execution
     *
     * @param fieldDefinition the graphql field definition
     * @param sourceObject the source object to get a value from
     * @param environmentSupplier a supplier of the [DataFetchingEnvironment] that creates it lazily
     * @return an uncompleted manualFuture that can be completed at later time
     */
    override fun get(
        fieldDefinition: GraphQLFieldDefinition,
        sourceObject: Any?,
        environmentSupplier: Supplier<DataFetchingEnvironment>
    ): CompletableFuture<Any?> {
        try {
            val fetchedValueRaw = originalDataFetcher.get(
                fieldDefinition,
                sourceObject,
                environmentSupplier
            )
            originalFuture = Async.toCompletableFuture(fetchedValueRaw)
        } catch (e: Exception) {
            originalExpressionException = e
        }
        return manualFuture
    }
}
