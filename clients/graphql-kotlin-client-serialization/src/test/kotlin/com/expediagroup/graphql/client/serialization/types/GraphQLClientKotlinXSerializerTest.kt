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

package com.expediagroup.graphql.client.serialization.types

import com.expediagroup.graphql.client.serialization.GraphQLClientKotlinxSerializer
import com.expediagroup.graphql.client.serialization.types.data.EnumQuery
import com.expediagroup.graphql.client.serialization.types.data.FirstQuery
import com.expediagroup.graphql.client.serialization.types.data.OtherQuery
import com.expediagroup.graphql.client.serialization.types.data.PolymorphicQuery
import com.expediagroup.graphql.client.serialization.types.data.ScalarQuery
import com.expediagroup.graphql.client.serialization.types.data.polymorphicquery.SecondInterfaceImplementation
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class GraphQLClientKotlinXSerializerTest {

    @Test
    fun `verify we can serialize GraphQLClientRequest`() {
        val testQuery = FirstQuery(FirstQuery.Variables(input = 1.0f))
        val expected =
            """{"operationName":"FirstQuery","query":"FIRST_QUERY","variables":{"input":1.0}}"""

        val serializer = GraphQLClientKotlinxSerializer()
        val result = serializer.serialize(testQuery)
        assertEquals(expected, result)
    }

    @Test
    fun `verify we can serialize batch GraphQLClientRequest`() {
        val queries = listOf(FirstQuery(FirstQuery.Variables(input = 1.0f)), OtherQuery())
        val expected =
            """[{"operationName":"FirstQuery","query":"FIRST_QUERY","variables":{"input":1.0}},{"operationName":"OtherQuery","query":"OTHER_QUERY","variables":null}]"""

        val serializer = GraphQLClientKotlinxSerializer()
        val result = serializer.serialize(queries)
        assertEquals(expected, result)
    }

    @Test
    fun `verify we can deserialize JacksonGraphQLResponse`() {
        val testQuery = FirstQuery(variables = FirstQuery.Variables())
        val expected = KotlinxGraphQLResponse(
            data = FirstQuery.Result("hello world"),
            errors = listOf(
                KotlinxGraphQLError(
                    message = "test error message",
                    locations = listOf(KotlinxGraphQLSourceLocation(1, 1)),
                    path = listOf("stringResult", 1, "leaf"),
                    extensions = mapOf("errorExt" to 123)
                )
            ),
            extensions = mapOf(
                "booleanVal" to false,
                "doubleVal" to 1.234,
                "intVal" to 1234,
                "listVal" to listOf("val1", 2),
                "mapVal" to mapOf("first" to 42, "second" to "whatever", "third" to null),
                "nullVal" to null
            )
        )

        val rawResponse =
            """{
            |  "data": {
            |    "stringResult": "hello world"
            |  },
            |  "errors": [{
            |    "message": "test error message",
            |    "locations": [{
            |      "line": 1,
            |      "column": 1
            |    }],
            |    "path": [
            |      "stringResult",
            |      1,
            |      "leaf"
            |    ],
            |    "extensions": {
            |      "errorExt": 123
            |    }
            |  }],
            |  "extensions": {
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

        val serializer = GraphQLClientKotlinxSerializer()
        val result = serializer.deserialize(rawResponse, testQuery.responseType())
        assertEquals(expected, result)
    }

    @Test
    fun `verify we can deserialize batch JacksonGraphQLResponse`() {
        val testQuery = FirstQuery(variables = FirstQuery.Variables())
        val otherQuery = OtherQuery()
        val expected = listOf(
            KotlinxGraphQLResponse(
                data = FirstQuery.Result("hello world"),
                errors = listOf(KotlinxGraphQLError(message = "test error message")),
                extensions = mapOf("extVal" to 123, "extList" to listOf("ext1", "ext2"), "extMap" to mapOf("1" to 1, "2" to 2))
            ),
            KotlinxGraphQLResponse(
                data = OtherQuery.Result(stringResult = "goodbye world", integerResult = 42)
            )
        )
        val rawResponses =
            """[{
            |  "data": { "stringResult" : "hello world" },
            |  "errors": [{ "message" : "test error message" }],
            |  "extensions" : { "extVal" : 123, "extList" : ["ext1", "ext2"], "extMap" : { "1" : 1, "2" : 2} }
            |}, {
            |  "data": { "stringResult" : "goodbye world", "integerResult" : 42 }
            |}]
        """.trimMargin()

        val serializer = GraphQLClientKotlinxSerializer()
        val result = serializer.deserialize(rawResponses, listOf(testQuery.responseType(), otherQuery.responseType()))
        assertEquals(expected, result)
    }

    @Test
    fun `verify we can deserialize polymorphic response`() {
        val polymorphicResponse =
            """{
            |  "data": {
            |    "polymorphicResult": {
            |      "__typename": "SecondInterfaceImplementation",
            |      "id": 123,
            |      "floatValue": 1.2
            |    }
            |  }
            |}
        """.trimMargin()
        val serializer = GraphQLClientKotlinxSerializer()
        val result = serializer.deserialize(polymorphicResponse, PolymorphicQuery().responseType())
        assertEquals(SecondInterfaceImplementation(123, 1.2f), result.data?.polymorphicResult)
    }

    @Test
    fun `verify we can deserialize custom scalars`() {
        val expectedUUID = UUID.randomUUID()
        val scalarResponse =
            """{
            |  "data": {
            |    "scalarAlias": "1234",
            |    "customScalar": "$expectedUUID"
            |  }
            |}
        """.trimMargin()
        val serializer = GraphQLClientKotlinxSerializer()
        val result = serializer.deserialize(scalarResponse, ScalarQuery().responseType())
        assertEquals("1234", result.data?.scalarAlias)
        assertEquals(expectedUUID, result.data?.customScalar?.value)
    }

    @Test
    fun `verify we can deserialize unknown enums`() {
        val unknownResponse =
            """{
            |  "data": { "enumResult": "INVALID" }
            |}
        """.trimMargin()

        val serializer = GraphQLClientKotlinxSerializer()
        val result = serializer.deserialize(unknownResponse, EnumQuery().responseType())
        assertEquals(EnumQuery.TestEnum.__UNKNOWN, result.data?.enumResult)
    }
}
