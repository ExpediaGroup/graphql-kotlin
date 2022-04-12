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

package com.expediagroup.graphql.client.serialization.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GraphQLErrorPathSerializerTest {

    private val expectedData = ErrorPathWrapper(
        path = listOf("root", 1, "leaf")
    )

    private val expectedSerialized =
        """{
        |  "path": [
        |    "root",
        |    1,
        |    "leaf"
        |  ]
        |}
        """.trimMargin()

    @Test
    @ExperimentalSerializationApi
    fun `verify serialization`() {
        val json = Json {
            prettyPrint = true
            // defaults to 4 spaces
            prettyPrintIndent = "  "
        }
        val serialized = json.encodeToString(expectedData)
        assertEquals(expectedSerialized, serialized)
    }

    @Test
    fun `verify deserialization`() {
        val json = Json
        val result = json.decodeFromString<ErrorPathWrapper>(expectedSerialized)
        assertEquals(expectedData, result)
    }

    @Serializable
    data class ErrorPathWrapper(
        val path: List<@Serializable(with = GraphQLErrorPathSerializer::class) Any>? = null
    )
}
