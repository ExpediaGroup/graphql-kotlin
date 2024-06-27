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

package com.expediagroup.graphql.client.serialization

import com.expediagroup.graphql.client.serialization.data.EmptyInputQuery
import com.expediagroup.graphql.client.serialization.data.EntitiesQuery
import com.expediagroup.graphql.client.serialization.data.EnumQuery
import com.expediagroup.graphql.client.serialization.data.FirstQuery
import com.expediagroup.graphql.client.serialization.data.InputQuery
import com.expediagroup.graphql.client.serialization.data.OptionalInputQuery
import com.expediagroup.graphql.client.serialization.data.OtherQuery
import com.expediagroup.graphql.client.serialization.data.PolymorphicQuery
import com.expediagroup.graphql.client.serialization.data.ScalarQuery
import com.expediagroup.graphql.client.serialization.data.enums.TestEnum
import com.expediagroup.graphql.client.serialization.data.polymorphicquery.SecondInterfaceImplementation
import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLError
import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLResponse
import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLSourceLocation
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class GraphQLClientKotlinXSerializerTest {

    private val serializer = GraphQLClientKotlinxSerializer {
        prettyPrint = true
    }

    @Test
    fun `verify we can serialize GraphQLClientRequest`() {
        val testQuery = FirstQuery(FirstQuery.Variables(input = 1.0f))
        val serialized = serializer.serialize(testQuery)
        val expected = """{
            |    "variables": {
            |        "input": 1.0
            |    },
            |    "query": "FIRST_QUERY",
            |    "operationName": "FirstQuery"
            |}
        """.trimMargin()

        assertEquals(expected, serialized)
    }

    @Test
    fun `verify we can serialize batch GraphQLClientRequest`() {
        val queries = listOf(FirstQuery(FirstQuery.Variables(input = 1.0f)), OtherQuery())

        val serialized = serializer.serialize(queries)
        val expected = """[{
            |    "variables": {
            |        "input": 1.0
            |    },
            |    "query": "FIRST_QUERY",
            |    "operationName": "FirstQuery"
            |},{
            |    "query": "OTHER_QUERY",
            |    "operationName": "OtherQuery"
            |}]
        """.trimMargin()

        assertEquals(expected, serialized)
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
        val scalarQuery = ScalarQuery(variables = ScalarQuery.Variables(alias = "1234", custom = randomUUID))

        val serialized = serializer.serialize(scalarQuery)
        val expected = """{
            |    "variables": {
            |        "alias": "1234",
            |        "custom": "$randomUUID"
            |    },
            |    "query": "SCALAR_QUERY",
            |    "operationName": "ScalarQuery"
            |}
        """.trimMargin()
        assertEquals(expected, serialized)
    }

    @Test
    fun `verify we can deserialize custom scalars`() {
        val expectedUUID = UUID.randomUUID()
        val scalarResponse =
            """{
            |  "data": {
            |    "scalarAlias": "1234",
            |    "customScalar": "$expectedUUID",
            |    "customScalarList": ["$expectedUUID"]
            |  }
            |}
            """.trimMargin()

        val result = serializer.deserialize(scalarResponse, ScalarQuery(ScalarQuery.Variables()).responseType())
        assertEquals("1234", result.data?.scalarAlias)
        assertEquals(expectedUUID, result.data?.customScalar)
        assertEquals(1, result.data?.customScalarList?.size)
        assertEquals(expectedUUID, result.data?.customScalarList?.get(0))
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
        val expected = """{
            |    "variables": {
            |        "enum": "three"
            |    },
            |    "query": "ENUM_QUERY",
            |    "operationName": "EnumQuery"
            |}
        """.trimMargin()
        assertEquals(expected, serialized)
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

    @Test
    fun `verify we can serialize optional inputs`() {
        val uuid = UUID.randomUUID()
        val query = OptionalInputQuery(
            variables = OptionalInputQuery.Variables(
                requiredInput = 123,
                optionalIntInput = OptionalInput.Defined(123),
                optionalStringInput = OptionalInput.Defined(null),
                // optionalBooleanInput = OptionalInput.Undefined // use default
                optionalUUIDInput = OptionalInput.Defined(uuid),
                optionalUUIDListInput = OptionalInput.Defined(listOf(uuid))
            )
        )
        val rawQuery =
            """{
            |    "variables": {
            |        "requiredInput": 123,
            |        "optionalIntInput": 123,
            |        "optionalStringInput": null,
            |        "optionalUUIDInput": "$uuid",
            |        "optionalUUIDListInput": [
            |            "$uuid"
            |        ]
            |    },
            |    "query": "OPTIONAL_INPUT_QUERY",
            |    "operationName": "OptionalInputQuery"
            |}
            """.trimMargin()

        val serialized = serializer.serialize(query)
        assertEquals(rawQuery, serialized)
    }

    @Test
    fun `verify serialization of null values and empty collections`() {
        val query = InputQuery(
            variables = InputQuery.Variables(
                requiredInput = 123,
                // nullableId = null, // use default
                nullableListNullableElements = listOf(null),
                nullableListNonNullableElements = null, // same as default
                nullableElementList = listOf("foo", null),
                nonNullableElementList = listOf()
            )
        )

        val expected = """{
            |    "variables": {
            |        "requiredInput": 123,
            |        "nullableListNullableElements": [
            |            null
            |        ],
            |        "nullableElementList": [
            |            "foo",
            |            null
            |        ],
            |        "nonNullableElementList": []
            |    },
            |    "query": "INPUT_QUERY",
            |    "operationName": "InputQuery"
            |}
        """.trimMargin()
        val serialized = serializer.serialize(query)
        assertEquals(expected, serialized)
    }

    @Test
    fun `verify serialization of empty variables`() {
        val query = EmptyInputQuery(
            variables = EmptyInputQuery.Variables()
        )

        val expected = """{
            |    "variables": {},
            |    "query": "EMPTY_INPUT_QUERY",
            |    "operationName": "EmptyInputQuery"
            |}
        """.trimMargin()
        val serialized = serializer.serialize(query)
        assertEquals(expected, serialized)
    }

    @Test
    fun `verify we can serialize non-primitive custom scalars`() {
        val entity = Json.decodeFromString<JsonObject>(
            """
            |{
            |  "__typename": "Product",
            |  "id": "apollo-federation"
            |}
            """.trimMargin()
        )
        val entitiesQuery = EntitiesQuery(variables = EntitiesQuery.Variables(representations = listOf(entity)))
        val expected =
            """{
            |    "variables": {
            |        "representations": [
            |            {
            |                "__typename": "Product",
            |                "id": "apollo-federation"
            |            }
            |        ]
            |    },
            |    "query": "ENTITIES_QUERY",
            |    "operationName": "EntitiesQuery"
            |}
            """.trimMargin()

        val serialized = serializer.serialize(entitiesQuery)
        assertEquals(expected, serialized)
    }
}
