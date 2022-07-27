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

package com.expediagroup.graphql.dataloader.instrumentation.extensions

import graphql.GraphQLContext
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.dataloader.DataLoaderFactory
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

class BatchLoaderEnvironmentExtensionsTest {
    @Test
    fun `BatchLoaderEnvironment should access to the DataFetchingEnvironment`() {
        val stringMapperDataLoader = DataLoaderFactory.newDataLoader<String, String> { keys, batchLoaderEnvironment ->
            CompletableFuture.completedFuture(
                keys.map { key ->
                    batchLoaderEnvironment.getGraphQLContext()?.get<(String) -> String>("StringModifierLambda")?.invoke(key)
                }
            )
        }

        val stringModifierLambda = spyk(
            { string: String -> string.uppercase() }
        )

        val dataFetchingEnvironment = mockk<DataFetchingEnvironment> {
            every { graphQlContext } returns GraphQLContext.newContext().put(
                "StringModifierLambda",
                stringModifierLambda
            ).build()
        }

        stringMapperDataLoader.load("hello", dataFetchingEnvironment)
        stringMapperDataLoader.load("world", dataFetchingEnvironment)

        stringMapperDataLoader.dispatch()

        verify(exactly = 1) {
            stringModifierLambda("hello")
            stringModifierLambda("world")
        }
    }
}
