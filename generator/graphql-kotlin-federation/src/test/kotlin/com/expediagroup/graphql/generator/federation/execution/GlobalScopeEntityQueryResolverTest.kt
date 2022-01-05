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

package com.expediagroup.graphql.generator.federation.execution

import com.expediagroup.graphql.generator.federation.data.UserResolver
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class GlobalScopeEntityQueryResolverTest {

    @Test
    fun `verify it returns a CompletableFuture`() {
        val resolver = GlobalScopeEntityResolver(listOf(UserResolver()))
        val representations = listOf(mapOf<String, Any>("__typename" to "User", "userId" to 123, "name" to "testName"))
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
        }

        val result = resolver.get(env).get()
        assertNotNull(result)
    }

}
