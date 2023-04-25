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

package com.expediagroup.graalvm.schema

import com.expediagroup.graalvm.schema.dataloader.EXAMPLE_LOADER
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

/**
 * Queries verifying async models.
 */
class AsyncQuery : Query {
    private val random: Random = Random(100)

    fun future(): CompletableFuture<Int> = CompletableFuture.completedFuture(random.nextInt())

    fun dataLoader(env: DataFetchingEnvironment, id: ID): CompletableFuture<String> = env.getDataLoader<ID, String>(EXAMPLE_LOADER).load(id)

    suspend fun coroutine(): Int = coroutineScope {
        delay(10)
        random.nextInt()
    }

    // TODO reactor monads
}
