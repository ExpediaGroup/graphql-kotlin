/*
 * Copyright 2023 Expedia, Inc
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
import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.testSchemaConfig
import com.expediagroup.graphql.generator.toSchema
import graphql.GraphQL
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

class OptionalInputTest {

    private val schema = toSchema(
        queries = listOf(TopLevelObject(OptionalInputQuery())),
        config = testSchemaConfig()
    )

    private val graphQL = GraphQL.newGraphQL(schema).build()

    @DisplayName("verify optional arguments can be correctly deserialized")
    @ParameterizedTest(name = "{index} ==> {1}")
    @MethodSource("optionalInputTestArguments")
    fun `verify optional arguments can be correctly deserialized`(query: String, expectedResult: String) {
        val result = graphQL.execute(query)
        val data: Map<String, String>? = result.getData()

        val optionalInputResults = data?.values?.first()
        assertEquals(expectedResult, optionalInputResults)
    }

    companion object {
        @JvmStatic
        fun optionalInputTestArguments(): Stream<Arguments> = Stream.of(
            Arguments.of("{ optionalScalarInput }", "input scalar was not specified"),
            Arguments.of("{ optionalScalarInput(input: null) }", "input scalar value: null"),
            Arguments.of("{ optionalScalarInput(input: \"ABC\") }", "input scalar value: ABC"),
            Arguments.of("{ optionalObjectInput }", "input object was not specified"),
            Arguments.of("{ optionalObjectInput(input: null) }", "input object value: null"),
            Arguments.of("{ optionalObjectInput(input: { id: 1, name: \"ABC\" } )  }", "input object value: SimpleArgument(id=1, name=ABC)"),
            Arguments.of("{ optionalListScalarInput }", "input was not specified"),
            Arguments.of("{ optionalListScalarInput(input: null) }", "input value: null"),
            Arguments.of("{ optionalListScalarInput(input: [\"ABC\"] )  }", "input value: [ABC]"),
            Arguments.of("{ optionalListInputObject }", "input was not specified"),
            Arguments.of("{ optionalListInputObject(input: null) }", "input value: null"),
            Arguments.of("{ optionalListInputObject(input: [{id: 1, name: \"ABC\"}] )  }", "input value: [SimpleArgument(id=1, name=ABC)]"),
            Arguments.of("{ inputWithOptionalScalarValues(input: { required: \"ABC\" }) }", "argument with optional scalar was not specified"),
            Arguments.of("{ inputWithOptionalScalarValues(input: { required: \"ABC\" optional: null }) }", "argument scalar value: null"),
            Arguments.of("{ inputWithOptionalScalarValues(input: { required: \"ABC\" optional: 1 }) }", "argument scalar value: 1"),
            Arguments.of("{ inputWithOptionalValues(input: { required: \"ABC\" }) }", "argument with optional object was not specified"),
            Arguments.of("{ inputWithOptionalValues(input: { required: \"ABC\" optional: null }) }", "argument object value: null"),
            Arguments.of("{ inputWithOptionalValues(input: { required: \"ABC\" optional: { id: 1, name: \"XYZ\" } }) }", "argument object value: SimpleArgument(id=1, name=XYZ)"),
            Arguments.of(
                "{ inputWithNestedOptionalValues(input: { optional: { nestedOptionalScalar: \"ABC\", nestedOptionalInt: null } } )}",
                "HasNestedOptionalArguments(optional=Defined(value=DeeplyNestedArguments(nestedOptional=UNDEFINED, nestedOptionalScalar=Defined(value=ABC), nestedOptionalInt=Defined(value=null))), optionalScalar=UNDEFINED)"
            )
        )
    }
}

data class SimpleArgument(
    val id: Int,
    val name: String
)

data class HasOptionalScalarArguments(
    val required: String,
    val optional: OptionalInput<Int>
)

data class HasOptionalArguments(
    val required: String,
    val optional: OptionalInput<SimpleArgument>
)

data class HasNestedOptionalArguments(
    val optional: OptionalInput<DeeplyNestedArguments> = OptionalInput.Undefined,
    val optionalScalar: OptionalInput<Int> = OptionalInput.Undefined
)

data class DeeplyNestedArguments(
    val nestedOptional: OptionalInput<SimpleArgument> = OptionalInput.Undefined,
    val nestedOptionalScalar: OptionalInput<String> = OptionalInput.Undefined,
    val nestedOptionalInt: OptionalInput<Int> = OptionalInput.Undefined
)

class OptionalInputQuery {
    fun optionalScalarInput(input: OptionalInput<String>): String = when (input) {
        is OptionalInput.Undefined -> "input scalar was not specified"
        is OptionalInput.Defined<String> -> "input scalar value: ${input.value}"
    }

    fun optionalObjectInput(input: OptionalInput<SimpleArgument>): String = when (input) {
        is OptionalInput.Undefined -> "input object was not specified"
        is OptionalInput.Defined<SimpleArgument> -> "input object value: ${input.value}"
    }

    fun optionalListScalarInput(input: OptionalInput<List<String>>): String = when (input) {
        is OptionalInput.Undefined -> "input was not specified"
        is OptionalInput.Defined -> "input value: ${input.value}"
    }

    fun optionalListInputObject(input: OptionalInput<List<SimpleArgument>>): String = when (input) {
        is OptionalInput.Undefined -> "input was not specified"
        is OptionalInput.Defined -> "input value: ${input.value}"
    }

    fun inputWithOptionalScalarValues(input: HasOptionalScalarArguments): String = when (input.optional) {
        is OptionalInput.Undefined -> "argument with optional scalar was not specified"
        is OptionalInput.Defined<Int> -> "argument scalar value: ${input.optional.value}"
    }

    fun inputWithOptionalValues(input: HasOptionalArguments): String = when (input.optional) {
        is OptionalInput.Undefined -> "argument with optional object was not specified"
        is OptionalInput.Defined<SimpleArgument> -> "argument object value: ${input.optional.value}"
    }

    fun inputWithNestedOptionalValues(input: HasNestedOptionalArguments): String = input.toString()
}
