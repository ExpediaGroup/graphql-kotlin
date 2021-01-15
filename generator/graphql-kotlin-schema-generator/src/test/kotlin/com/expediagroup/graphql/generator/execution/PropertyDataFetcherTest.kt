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

package com.expediagroup.graphql.generator.execution

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.testSchemaConfig
import com.expediagroup.graphql.generator.toSchema
import graphql.GraphQL
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PropertyDataFetcherTest {

    private val schema = toSchema(
        queries = listOf(TopLevelObject(PrefixedQuery())),
        config = testSchemaConfig
    )
    private val graphQL = GraphQL.newGraphQL(schema).build()

    @Test
    fun `verify null source returns null`() {
        val dataFetcher = PropertyDataFetcher(propertyGetter = PrefixedFields::isBooleanValue.getter)
        val mockEnvironment: DataFetchingEnvironment = mockk {
            every { getSource<Any>() } returns null
        }
        assertNull(dataFetcher.get(mockEnvironment))
    }

    @Test
    fun `verify is prefix resolution for boolean field`() {
        val result = graphQL.execute("{ prefixed { booleanValue isBooleanValue } }")
        val data: Map<String, Map<String, Any>>? = result.getData()

        val prefixedResults = data?.get("prefixed")
        assertNotNull(prefixedResults)
        assertEquals(2, prefixedResults.size)
        assertEquals(true, prefixedResults["booleanValue"])
        assertEquals(false, prefixedResults["isBooleanValue"])
    }

    @Test
    fun `verify properties are correctly resolved`() {
        val result = graphQL.execute("{ prefixed { islandName isWhatever } }")
        val data: Map<String, Map<String, Any>>? = result.getData()

        val prefixedResults = data?.get("prefixed")
        assertNotNull(prefixedResults)
        assertEquals(2, prefixedResults.size)
        assertEquals("Iceland", prefixedResults["islandName"])
        assertEquals("whatever", prefixedResults["isWhatever"])
    }

    class PrefixedQuery {
        fun prefixed() = PrefixedFields()
    }

    data class PrefixedFields(
        val islandName: String = "Iceland",
        val booleanValue: Boolean = true,
        val isBooleanValue: Boolean = false,
        val isWhatever: String = "whatever"
    )
}
