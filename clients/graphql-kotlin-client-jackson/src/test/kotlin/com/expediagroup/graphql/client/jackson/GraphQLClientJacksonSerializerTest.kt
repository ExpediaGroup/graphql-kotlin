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

import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLError
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLResponse
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals

class GraphQLClientJacksonSerializerTest {

    @Test
    fun `verify we can serialize GraphQLClientRequest`() {
        val testQuery = TestQuery(TestQuery.Variables(input = 1.0f))
        val expected =
            """{
            |  "query": "TEST_QUERY",
            |  "operationName": "TestQuery",
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
        val queries = listOf(TestQuery(TestQuery.Variables(input = 1.0f)), TestOtherQuery())
        val expected =
            """[{
            |  "query": "TEST_QUERY",
            |  "operationName": "TestQuery",
            |  "variables": { "input": 1.0 }
            |},{
            |  "query": "OTHER_QUERY",
            |  "operationName": "TestOtherQuery",
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
        val testQuery = TestQuery(variables = TestQuery.Variables())
        val expected = JacksonGraphQLResponse(
            data = TestQuery.Result("hello world"),
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
        val testQuery = TestQuery(variables = TestQuery.Variables())
        val otherQuery = TestOtherQuery()
        val expected = listOf(
            JacksonGraphQLResponse(
                data = TestQuery.Result("hello world"),
                errors = listOf(JacksonGraphQLError(message = "test error message")),
                extensions = mapOf("extVal" to 123, "extList" to listOf("ext1", "ext2"), "extMap" to mapOf("1" to 1, "2" to 2))
            ),
            JacksonGraphQLResponse(
                data = TestOtherQuery.Result(stringResult = "goodbye world", integerResult = 42)
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

    class TestQuery(
        override val variables: Variables
    ) : GraphQLClientRequest<TestQuery.Result> {
        override val query: String = "TEST_QUERY"

        override val operationName: String = "TestQuery"

        override fun responseType(): KClass<Result> = Result::class

        data class Variables(
            val input: Float? = null
        )

        data class Result(
            val stringResult: String
        )
    }

    class TestOtherQuery : GraphQLClientRequest<TestOtherQuery.Result> {
        override val query: String = "OTHER_QUERY"

        override val operationName: String = "TestOtherQuery"

        override fun responseType(): KClass<Result> = Result::class

        data class Result(
            val stringResult: String,
            val integerResult: Int
        )
    }
}
