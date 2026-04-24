/*
 * Copyright 2024 Expedia, Inc
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

package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.generator.annotations.GraphQLName
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DataFetchingEnvironmentExtensionsTest {

    data class SimpleInput(val foo: String, val bar: String? = null)
    data class RenamedInput(@GraphQLName("baz") val foo: String)
    data class NestedInput(val inner: SimpleInput, val tag: String)

    @Test
    fun `getArgumentsAs coerces arguments to typed Kotlin object`() {
        val environment = mockk<DataFetchingEnvironment> {
            every { arguments } returns mapOf("foo" to "hello", "bar" to "world")
        }

        val result = environment.getArgumentsAs<SimpleInput>()

        assertEquals("hello", result.foo)
        assertEquals("world", result.bar)
    }

    @Test
    fun `getArgumentsAs respects default parameter values for absent fields`() {
        val environment = mockk<DataFetchingEnvironment> {
            every { arguments } returns mapOf("foo" to "hello")
        }

        val result = environment.getArgumentsAs(SimpleInput::class)

        assertEquals("hello", result.foo)
        assertEquals(null, result.bar)
    }

    @Test
    fun `getArgumentsAs resolves field names via GraphQLName`() {
        val environment = mockk<DataFetchingEnvironment> {
            every { arguments } returns mapOf("baz" to "renamed")
        }

        val result = environment.getArgumentsAs<RenamedInput>()

        assertEquals("renamed", result.foo)
    }

    @Test
    fun `getArgumentsAs passes through already-coerced field values`() {
        // Simulates what environment.arguments looks like after graphql-java has run
        // custom scalar coercers — the field value is already the target type, not a raw string.
        val preCoerced = SimpleInput("already", "coerced")
        val environment = mockk<DataFetchingEnvironment> {
            every { arguments } returns mapOf("inner" to preCoerced, "tag" to "test")
        }

        val result = environment.getArgumentsAs<NestedInput>()

        assertEquals(preCoerced, result.inner)
        assertEquals("test", result.tag)
    }
}
