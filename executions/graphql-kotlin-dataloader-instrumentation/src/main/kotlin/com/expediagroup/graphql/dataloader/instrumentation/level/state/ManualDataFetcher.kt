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

import graphql.schema.DataFetcher
import java.util.concurrent.CompletableFuture

/**
 * DataFetcher Decorator that allows manual completion of dataFetchers
 */
abstract class ManualDataFetcher : DataFetcher<CompletableFuture<Any?>> {
    val manualFuture: CompletableFuture<Any?> = CompletableFuture()
    var originalFuture: CompletableFuture<Any?>? = null
    var originalExpressionException: Exception? = null

    /**
     * Manually complete the [manualFuture] by handling the [originalFuture]
     */
    fun complete() {
        when {
            originalExpressionException != null -> manualFuture.completeExceptionally(originalExpressionException)
            else -> originalFuture?.handle { result, exception ->
                when {
                    exception != null -> manualFuture.completeExceptionally(exception)
                    else -> manualFuture.complete(result)
                }
            }
        }
    }
}
