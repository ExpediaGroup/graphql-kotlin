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

package com.expediagroup.graphql.client.serialization

import com.expediagroup.graphql.client.serialization.data.EnumQuery
import com.expediagroup.graphql.client.serialization.data.FirstQuery
import com.expediagroup.graphql.client.serialization.data.OtherQuery
import com.expediagroup.graphql.client.serialization.data.PolymorphicQuery
import com.expediagroup.graphql.client.serialization.data.ScalarQuery
import com.expediagroup.graphql.client.serialization.data.enums.TestEnum
import com.expediagroup.graphql.client.serialization.data.polymorphicquery.SecondInterfaceImplementation
import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLError
import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLResponse
import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLSourceLocation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class GraphQLClientKotlinXSerializerTest {

    private val serializer = GraphQLClientKotlinxSerializer()
    private val json = Json

    @Test
    fun `verify we can serialize GraphQLClientRequest`() {
        val testQuery = FirstQuery(FirstQuery.Variables(input = 1.0f))
        val result = serializer.serialize(testQuery)
        val deserialized: FirstQuery = json.decodeFromString(result)
        assertEquals(testQuery.variables, deserialized.variables)
    }

    @Test
    fun `verify we can serialize batch GraphQLClientRequest`() {
        val queries = listOf(FirstQuery(FirstQuery.Variables(input = 1.0f)), OtherQuery())

        val result = serializer.serialize(queries)
        val serializedQueries = result.substring(1, result.length - 1).split(Regex("(?<=\\}),(?=\\{)"))
        assertEquals(2, serializedQueries.size)

        val first: FirstQuery = json.decodeFromString(serializedQueries[0])
        assertEquals(queries[0].variables, first.variables)

        val second: OtherQuery = json.decodeFromString(serializedQueries[1])
        assertEquals(queries[1].variables, second.variables)
    }

    @Test
    fun `verify we can deserialize KotlinxGraphQLResponse`() {
        val testQuery = FirstQuery(variables = FirstQuery.Variables())
        val expected = KotlinxGraphQLResponse(
            data = FirstQuery.Result("hello world"),
            errors = listOf(
                KotlinxGraphQLError(
                    message = "test error message",
                    locations = listOf(KotlinxGraphQLSourceLocation(1, 1)),
                    path = listOf("firstQuery", 0),
                    extensions = mapOf("errorExt" to 123)
                )
            ),
            extensions = mapOf(
                "extBool" to true,
                "extDouble" to 1.5,
                "extInt" to 123,
                "extList" to listOf("ext1", "ext2"),
                "extMap" to mapOf("1" to 1, "2" to 2.0),
                "extNull" to null,
                "extString" to "extra"
            )
        )
        val rawResponse =
            """{
            |  "data": { "stringResult" : "hello world" },
            |  "errors": [{
            |    "message": "test error message",
            |    "locations": [{ "line": 1, "column": 1 }],
            |    "path": [ "firstQuery", 0 ],
            |    "extensions": {
            |      "errorExt": 123
            |    }
            |  }],
            |  "extensions" : {
            |    "extBool": true,
            |    "extDouble": 1.5,
            |    "extInt": 123,
            |    "extList": ["ext1", "ext2"],
            |    "extMap": { "1" : 1, "2" : 2.0 },
            |    "extNull": null,
            |    "extString": "extra"
            |  }
            |}
        """.trimMargin()

        val result = serializer.deserialize(rawResponse, testQuery.responseType())
        assertEquals(expected, result)
    }

    @Test
    fun `verify we can deserialize batch KotlinxGraphQLResponse`() {
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

        val result = serializer.deserialize(polymorphicResponse, PolymorphicQuery().responseType())
        assertEquals(SecondInterfaceImplementation(123, 1.2f), result.data?.polymorphicResult)
    }

    @Test
    fun `verify we can serialize custom scalars`() {
        val randomUUID = UUID.randomUUID()
        val scalarQuery = ScalarQuery(variables = ScalarQuery.Variables(alias = "1234", custom = com.expediagroup.graphql.client.serialization.data.scalars.UUID(randomUUID)))

        val serialized = serializer.serialize(scalarQuery)
        val deserialized: ScalarQuery = json.decodeFromString(serialized)
        assertEquals(scalarQuery.variables, deserialized.variables)
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

        val result = serializer.deserialize(scalarResponse, ScalarQuery(ScalarQuery.Variables()).responseType())
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

        val result = serializer.deserialize(unknownResponse, EnumQuery(EnumQuery.Variables()).responseType())
        assertEquals(TestEnum.__UNKNOWN, result.data?.enumResult)
    }

    @Test
    fun `verify we can serialize enums with custom names`() {
        val query = EnumQuery(variables = EnumQuery.Variables(enum = TestEnum.THREE))

        val serialized = serializer.serialize(query)
        val deserialized: EnumQuery = json.decodeFromString(serialized)
        assertEquals(query.variables, deserialized.variables)
    }

    @Test
    fun `verify we can deserialize enums with custom names`() {
        val rawResponse =
            """{
            |  "data": { "enumResult": "three" }
            |}
        """.trimMargin()
        val deserialized = serializer.deserialize(rawResponse, EnumQuery(EnumQuery.Variables()).responseType())
        assertEquals(TestEnum.THREE, deserialized.data?.enumResult)
    }
}
