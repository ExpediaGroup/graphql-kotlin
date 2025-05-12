/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.generator.test.integration

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.internal.types.GenerateEnumTest
import com.expediagroup.graphql.generator.testSchemaConfig
import com.expediagroup.graphql.generator.toSchema
import graphql.GraphQL
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Suppress(
    "Detekt.UnusedPrivateMember",
)
class CustomEnumExecutionTest {

    private val schema = toSchema(
        queries = listOf(TopLevelObject(QueryObject())),
        config = testSchemaConfig()
    )
    private val graphQL: GraphQL = GraphQL.newGraphQL(schema).build()

    @Test
    fun `a custom enum name can be used as output`() {
        val result = graphQL.execute("{ enumOutputs }")
        val data: Map<String, List<String>>? = result.getData()

        assertEquals(emptyList(), result.errors)
        assertEquals(listOf("ONE", "TWO", "THREE", "customFour"), data?.get("enumOutputs"))
    }

    @Test
    fun `an implicit enum name be used as input`() {
        val result = graphQL.execute("{ enumInput(value: THREE) }")
        val data: Map<String, String>? = result.getData()

        assertEquals(emptyList(), result.errors)
        assertEquals("hello, THREE", data?.get("enumInput"))
    }

    @Test
    fun `a custom enum name be used as input`() {
        val result = graphQL.execute("{ enumInput(value: customFour) }")
        val data: Map<String, String>? = result.getData()

        assertEquals(emptyList(), result.errors)
        assertEquals("hello, FOUR", data?.get("enumInput"))
    }

    class QueryObject {
        fun enumOutputs(): List<GenerateEnumTest.MyTestEnum> = GenerateEnumTest.MyTestEnum.entries
        fun enumInput(value: GenerateEnumTest.MyTestEnum) = "hello, $value"
    }
}
