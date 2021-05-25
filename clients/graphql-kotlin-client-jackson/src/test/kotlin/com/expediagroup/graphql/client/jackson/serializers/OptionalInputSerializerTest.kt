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

package com.expediagroup.graphql.client.jackson.serializers

import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OptionalInputSerializerTest {

    private val mapper = jacksonObjectMapper()
    init {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    }

    @Test
    fun `verify undefined value is serialized to empty JSON`() {
        val undefined = InputWrapper()
        assertEquals("{}", mapper.writeValueAsString(undefined))
    }

    @Test
    fun `verify null value is serialized correctly`() {
        val explicitNull = InputWrapper(
            optionalInput = OptionalInput.Defined(null),
            optionalInputObject = OptionalInput.Defined(null),
            optionalInputList = OptionalInput.Defined(null)
        )
        assertEquals("""{"optionalInput":null,"optionalInputObject":null,"optionalInputList":null}""", mapper.writeValueAsString(explicitNull))
    }

    @Test
    fun `verify defined values are serialized correctly`() {
        val defined = InputWrapper(
            optionalInput = OptionalInput.Defined(123),
            optionalInputObject = OptionalInput.Defined(BasicInput(OptionalInput.Defined("foo"))),
            optionalInputList = OptionalInput.Defined(listOf("a", "b", "c"))
        )
        assertEquals("""{"optionalInput":123,"optionalInputObject":{"name":"foo"},"optionalInputList":["a","b","c"]}""", mapper.writeValueAsString(defined))
    }

    @Test
    fun `verify inner undefined values are serialized correctly`() {
        val nestedUndefined = InputWrapper(optionalInputObject = OptionalInput.Defined(BasicInput(name = OptionalInput.Undefined)))
        assertEquals("""{"optionalInputObject":{}}""", mapper.writeValueAsString(nestedUndefined))
    }

    data class InputWrapper(
        val optionalInput: OptionalInput<Int> = OptionalInput.Undefined,
        val optionalInputObject: OptionalInput<BasicInput> = OptionalInput.Undefined,
        val optionalInputList: OptionalInput<List<String>> = OptionalInput.Undefined
    )

    data class BasicInput(
        val name: OptionalInput<String> = OptionalInput.Undefined
    )
}
