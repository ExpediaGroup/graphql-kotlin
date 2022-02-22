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

package com.expediagroup.graphql.transactionbatcher.transaction

import java.util.concurrent.CompletableFuture

/**
 * convenient class to store the reference of a [future] of type [TOutput]
 * that will be resolved asynchronously at later point in time by using [input] as source
 * it supports deduplication by using the [key] field
 */
data class BatchableTransaction<TInput, TOutput>(
    val input: TInput,
    val future: CompletableFuture<TOutput>,
    val key: String
)
