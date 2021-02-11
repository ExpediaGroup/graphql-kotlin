/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.server.extensions

import com.expediagroup.graphql.server.exception.MissingDataLoaderException
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DataFetchingEnvironmentExtensionsKtTest {

    @Test
    fun `getting a dataloader passes when a matching name is found`() {
        val dataFetchingEnvironment: DataFetchingEnvironment = mockk {
            every { getContext<Any>() } returns mockk()
            every { getDataLoader<String, String>("foo") } returns mockk {
                every { load("bar", any()) } returns CompletableFuture.completedFuture("123")
            }
        }

        val result: CompletableFuture<String> = dataFetchingEnvironment.getValueFromDataLoader("foo", "bar")

        assertEquals("123", result.get())
    }

    @Test
    fun `getting a dataloader throws exception when name not found`() {
        val dataFetchingEnvironment: DataFetchingEnvironment = mockk {
            every { getContext<Any>() } returns mockk()
            every { getDataLoader<String, String>("foo") } returns null
        }

        assertFailsWith(MissingDataLoaderException::class) {
            dataFetchingEnvironment.getValueFromDataLoader<String, String>("foo", "bar")
        }
    }
}
