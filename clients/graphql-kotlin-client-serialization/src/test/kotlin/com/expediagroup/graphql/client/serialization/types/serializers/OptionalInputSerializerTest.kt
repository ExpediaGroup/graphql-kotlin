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

package com.expediagroup.graphql.client.serialization.types.serializers

import com.expediagroup.graphql.client.serialization.types.OptionalInput
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OptionalInputSerializerTest {

    private val json = Json {
        encodeDefaults = false
    }

    @Test
    fun `verify undefined value is serialized to empty JSON`() {
        val undefined = InputWrapper()
        assertEquals("{}", json.encodeToString(undefined))
    }

    @Test
    fun `verify null value is serialized correctly`() {
        val explicitNull = InputWrapper(
            optionalInput = OptionalInput.Defined(null),
            optionalInputObject = OptionalInput.Defined(null),
            optionalInputList = OptionalInput.Defined(null)
        )
        assertEquals("""{"optionalInput":null,"optionalInputObject":null,"optionalInputList":null}""", json.encodeToString(explicitNull))
    }

    @Test
    fun `verify defined values are serialized correctly`() {
        val defined = InputWrapper(
            optionalInput = OptionalInput.Defined(123),
            optionalInputObject = OptionalInput.Defined(BasicInput(OptionalInput.Defined("foo"))),
            optionalInputList = OptionalInput.Defined(listOf("a", "b", "c"))
        )
        assertEquals("""{"optionalInput":123,"optionalInputObject":{"name":"foo"},"optionalInputList":["a","b","c"]}""", json.encodeToString(defined))
    }

    @Test
    fun `verify inner undefined values are serialized correctly`() {
        val nestedUndefined = InputWrapper(optionalInputObject = OptionalInput.Defined(BasicInput(name = OptionalInput.Undefined)))
        assertEquals("""{"optionalInputObject":{}}""", json.encodeToString(nestedUndefined))
    }

    @Serializable
    data class InputWrapper(
        val optionalInput: OptionalInput<Int> = OptionalInput.Undefined,
        val optionalInputObject: OptionalInput<BasicInput> = OptionalInput.Undefined,
        val optionalInputList: OptionalInput<List<String>> = OptionalInput.Undefined
    )

    @Serializable
    data class BasicInput(
        val name: OptionalInput<String> = OptionalInput.Undefined
    )
}
