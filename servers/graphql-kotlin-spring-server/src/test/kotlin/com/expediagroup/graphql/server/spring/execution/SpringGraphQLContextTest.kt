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

package com.expediagroup.graphql.server.spring.execution

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.server.ServerRequest
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SpringGraphQLContextTest {

    @Test
    fun `getHTTPRequestHeader should get the value from http request`() {
        val mockRequest: ServerRequest = mockk {
            every { headers() } returns mockk {
                every { firstHeader("foo") } returns "bar"
            }
        }
        val context = SpringGraphQLContext(mockRequest)

        assertEquals("bar", context.getHTTPRequestHeader("foo"))
    }

    @Test
    fun `getHTTPRequestHeader should return null if there is no header`() {
        val mockRequest: ServerRequest = mockk {
            every { headers() } returns mockk {
                every { firstHeader("foo") } returns null
            }
        }
        val context = SpringGraphQLContext(mockRequest)

        assertNull(context.getHTTPRequestHeader("foo"))
    }
}
