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

class AnyKSerializerTest {

    private val expectedData = AnyMap(
        data = mapOf(
            "booleanVal" to false,
            "doubleVal" to 1.234,
            "intVal" to 1234,
            "listVal" to listOf("val1", 2),
            "mapVal" to mapOf("first" to 42, "second" to "whatever", "third" to null),
            "nullVal" to null
        )
    )
    private val expectedSerialized =
        """{
        |  "data": {
        |    "booleanVal": false,
        |    "doubleVal": 1.234,
        |    "intVal": 1234,
        |    "listVal": [
        |      "val1",
        |      2
        |    ],
        |    "mapVal": {
        |      "first": 42,
        |      "second": "whatever",
        |      "third": null
        |    },
        |    "nullVal": null
        |  }
        |}
        """.trimMargin()

    @Test
    @ExperimentalSerializationApi
    fun `verify serialization logic`() {
        val json = Json {
            prettyPrint = true
            // defaults to 4 spaces
            prettyPrintIndent = "  "
        }
        val serialized = json.encodeToString(expectedData)
        assertEquals(expectedSerialized, serialized)
    }

    @Test
    @ExperimentalSerializationApi
    fun `verify object serialization logic`() {
        val json = Json {
            prettyPrint = true
            // defaults to 4 spaces
            prettyPrintIndent = "  "
        }
        val data = AnyMap(data = mapOf("object" to Foo("baz")))
        val expected = """{
            |  "data": {
            |    "object": {
            |      "bar": "baz"
            |    }
            |  }
            |}
        """.trimMargin()

        val serialized = json.encodeToString(data)
        assertEquals(expected, serialized)
    }

    @Test
    fun `verify deserialization logic`() {
        val json = Json
        val result = json.decodeFromString<AnyMap>(expectedSerialized)
        assertEquals(expectedData, result)
    }

    @Serializable
    data class AnyMap(
        val data: Map<String, @Serializable(with = AnyKSerializer::class) Any?>
    )
    @Serializable
    data class Foo(val bar: String)
}
