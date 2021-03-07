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

package com.expediagroup.graphql.client.jackson

import com.expediagroup.graphql.client.jackson.data.EnumQuery
import com.expediagroup.graphql.client.jackson.data.FirstQuery
import com.expediagroup.graphql.client.jackson.data.OtherQuery
import com.expediagroup.graphql.client.jackson.data.PolymorphicQuery
import com.expediagroup.graphql.client.jackson.data.ScalarQuery
import com.expediagroup.graphql.client.jackson.data.polymorphicquery.SecondInterfaceImplementation
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLError
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class GraphQLClientJacksonSerializerTest {

    @Test
    fun `verify we can serialize GraphQLClientRequest`() {
        val testQuery = FirstQuery(FirstQuery.Variables(input = 1.0f))
        val expected =
            """{
            |  "query": "FIRST_QUERY",
            |  "operationName": "FirstQuery",
            |  "variables": { "input": 1.0 }
            |}
        """.trimMargin()

        val mapper = jacksonObjectMapper()
        val serializer = GraphQLClientJacksonSerializer(mapper)
        val result = serializer.serialize(testQuery)
        assertEquals(mapper.readTree(expected), mapper.readTree(result))
    }

    @Test
    fun `verify we can serialize batch GraphQLClientRequest`() {
        val queries = listOf(FirstQuery(FirstQuery.Variables(input = 1.0f)), OtherQuery())
        val expected =
            """[{
            |  "query": "FIRST_QUERY",
            |  "operationName": "FirstQuery",
            |  "variables": { "input": 1.0 }
            |},{
            |  "query": "OTHER_QUERY",
            |  "operationName": "OtherQuery",
            |  "variables": null
            |}]
        """.trimMargin()

        val mapper = jacksonObjectMapper()
        val serializer = GraphQLClientJacksonSerializer(mapper)
        val result = serializer.serialize(queries)
        assertEquals(mapper.readTree(expected), mapper.readTree(result))
    }

    @Test
    fun `verify we can deserialize JacksonGraphQLResponse`() {
        val testQuery = FirstQuery(variables = FirstQuery.Variables())
        val expected = JacksonGraphQLResponse(
            data = FirstQuery.Result("hello world"),
            errors = listOf(JacksonGraphQLError(message = "test error message")),
            extensions = mapOf("extVal" to 123, "extList" to listOf("ext1", "ext2"), "extMap" to mapOf("1" to 1, "2" to 2))
        )
        val rawResponse =
            """{
            |  "data": { "stringResult" : "hello world" },
            |  "errors": [{ "message" : "test error message" }],
            |  "extensions" : { "extVal" : 123, "extList" : ["ext1", "ext2"], "extMap" : { "1" : 1, "2" : 2} }
            |}
        """.trimMargin()

        val serializer = GraphQLClientJacksonSerializer()
        val result = serializer.deserialize(rawResponse, testQuery.responseType())
        assertEquals(expected, result)
    }

    @Test
    fun `verify we can deserialize batch JacksonGraphQLResponse`() {
        val testQuery = FirstQuery(variables = FirstQuery.Variables())
        val otherQuery = OtherQuery()
        val expected = listOf(
            JacksonGraphQLResponse(
                data = FirstQuery.Result("hello world"),
                errors = listOf(JacksonGraphQLError(message = "test error message")),
                extensions = mapOf("extVal" to 123, "extList" to listOf("ext1", "ext2"), "extMap" to mapOf("1" to 1, "2" to 2))
            ),
            JacksonGraphQLResponse(
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

        val serializer = GraphQLClientJacksonSerializer()
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
        val serializer = GraphQLClientJacksonSerializer()
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
        val serializer = GraphQLClientJacksonSerializer()
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

        val serializer = GraphQLClientJacksonSerializer()
        val result = serializer.deserialize(unknownResponse, EnumQuery().responseType())
        assertEquals(EnumQuery.TestEnum.__UNKNOWN, result.data?.enumResult)
    }
}
