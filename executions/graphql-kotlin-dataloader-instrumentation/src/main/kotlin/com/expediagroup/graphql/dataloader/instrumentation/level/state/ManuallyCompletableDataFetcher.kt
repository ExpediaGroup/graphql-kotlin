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
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

/**
 * DataFetcher Decorator that stores the original dataFetcher result (it's always a completable future)
 * it stores the [originalFuture] as property and returns an uncompleted [manualFuture]
 * then at later point manually call [complete] to complete the [manualFuture] with the [originalFuture] result
 * to let ExecutionStrategy handle all futures
 *
 * @param originalDataFetcher original dataFetcher to be decorated
 */
class ManuallyCompletableDataFetcher(
    private val originalDataFetcher: DataFetcher<*>
) : ManualDataFetcher() {
    /**
     * when attempting to get the value from dataFetcher, execute the [originalDataFetcher]
     * and store the resulting future [originalFuture] and a possible [originalExpressionException] if
     * a synchronous exception was thrown during the execution
     *
     * @param environment dataFetchingEnvironment with information about the field
     * @return an uncompleted manualFuture that can be completed at later time
     */
    override fun get(environment: DataFetchingEnvironment): CompletableFuture<Any?> {
        try {
            val fetchedValueRaw = originalDataFetcher.get(environment)
            originalFuture = Async.toCompletableFuture(fetchedValueRaw)
        } catch (e: Exception) {
            originalExpressionException = e
        }
        return manualFuture
    }
}
