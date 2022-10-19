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

package com.expediagroup.graphql.client.jackson

import com.expediagroup.graphql.client.jackson.data.EmptyInputQuery
import com.expediagroup.graphql.client.jackson.data.EntitiesQuery
import com.expediagroup.graphql.client.jackson.data.EnumQuery
import com.expediagroup.graphql.client.jackson.data.FirstQuery
import com.expediagroup.graphql.client.jackson.data.InputQuery
import com.expediagroup.graphql.client.jackson.data.OptionalInputQuery
import com.expediagroup.graphql.client.jackson.data.OtherQuery
import com.expediagroup.graphql.client.jackson.data.PolymorphicQuery
import com.expediagroup.graphql.client.jackson.data.ScalarQuery
import com.expediagroup.graphql.client.jackson.data.enums.TestEnum
import com.expediagroup.graphql.client.jackson.data.polymorphicquery.SecondInterfaceImplementation
import com.expediagroup.graphql.client.jackson.data.scalars.ProductEntityRepresentation
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLError
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLResponse
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLSourceLocation
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class GraphQLClientJacksonSerializerTest {

    private val testMapper = jacksonObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT)
    private val serializer = GraphQLClientJacksonSerializer(testMapper)

    @Test
    fun `verify we can serialize GraphQLClientRequest`() {
        val testQuery = FirstQuery(FirstQuery.Variables(input = 1.0f))
        val expected =
            """{
            |  "variables" : {
            |    "input" : 1.0
            |  },
            |  "query" : "FIRST_QUERY",
            |  "operationName" : "FirstQuery"
            |}
            """.trimMargin()

        val serialized = serializer.serialize(testQuery)
        assertEquals(expected, serialized)
    }

    @Test
    fun `verify we can serialize batch GraphQLClientRequest`() {
        val queries = listOf(FirstQuery(FirstQuery.Variables(input = 1.0f)), OtherQuery())
        val expected =
            """[ {
            |  "variables" : {
            |    "input" : 1.0
            |  },
            |  "query" : "FIRST_QUERY",
            |  "operationName" : "FirstQuery"
            |}, {
            |  "query" : "OTHER_QUERY",
            |  "operationName" : "OtherQuery"
            |} ]
            """.trimMargin()

        val serialized = serializer.serialize(queries)
        assertEquals(expected, serialized)
    }

    @Test
    fun `verify we can deserialize JacksonGraphQLResponse`() {
        val testQuery = FirstQuery(variables = FirstQuery.Variables())
        val expected = JacksonGraphQLResponse(
            data = FirstQuery.Result("hello world"),
            errors = listOf(
                JacksonGraphQLError(
                    message = "test error message",
                    locations = listOf(JacksonGraphQLSourceLocation(1, 1)),
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
        val expected =
            """{
            |  "variables" : {
            |    "alias" : "1234",
            |    "custom" : "$randomUUID"
            |  },
            |  "query" : "SCALAR_QUERY",
            |  "operationName" : "ScalarQuery"
            |}
            """.trimMargin()

        val serialized = serializer.serialize(scalarQuery)
        assertEquals(expected, serialized)
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
        assertEquals(expectedUUID, result.data?.customScalar)
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
        val expected =
            """{
            |  "variables" : {
            |    "enum" : "three"
            |  },
            |  "query" : "ENUM_QUERY",
            |  "operationName" : "EnumQuery"
            |}
            """.trimMargin()

        val serialized = serializer.serialize(query)
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
        val query = OptionalInputQuery(
            variables = OptionalInputQuery.Variables(
                requiredInput = 123,
                optionalIntInput = OptionalInput.Defined(123),
                optionalStringInput = OptionalInput.Defined(null)
                // optionalBooleanInput = OptionalInput.Undefined // use default
            )
        )
        val expected =
            """{
            |  "variables" : {
            |    "requiredInput" : 123,
            |    "optionalIntInput" : 123,
            |    "optionalStringInput" : null
            |  },
            |  "query" : "OPTIONAL_INPUT_QUERY",
            |  "operationName" : "OptionalInputQuery"
            |}
            """.trimMargin()

        val serialized = serializer.serialize(query)
        assertEquals(expected, serialized)
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
            |  "variables" : {
            |    "requiredInput" : 123,
            |    "nullableListNullableElements" : [ null ],
            |    "nullableElementList" : [ "foo", null ],
            |    "nonNullableElementList" : [ ],
            |    "inputObject" : {
            |      "isNotBoolean" : "yes",
            |      "NOT" : false,
            |      "pID" : "1"
            |    }
            |  },
            |  "query" : "INPUT_QUERY",
            |  "operationName" : "InputQuery"
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
            |  "variables" : { },
            |  "query" : "EMPTY_INPUT_QUERY",
            |  "operationName" : "EmptyInputQuery"
            |}
        """.trimMargin()
        val serialized = serializer.serialize(query)
        assertEquals(expected, serialized)
    }

    @Test
    fun `verify we can serialize non-primitive custom scalars`() {
        val entitiesQuery = EntitiesQuery(variables = EntitiesQuery.Variables(representations = listOf(ProductEntityRepresentation(id = "apollo-federation"))))
        val expected =
            """{
            |  "variables" : {
            |    "representations" : [ {
            |      "id" : "apollo-federation",
            |      "__typename" : "Product"
            |    } ]
            |  },
            |  "query" : "ENTITIES_QUERY",
            |  "operationName" : "EntitiesQuery"
            |}
            """.trimMargin()

        val serialized = serializer.serialize(entitiesQuery)
        assertEquals(expected, serialized)
    }
}
