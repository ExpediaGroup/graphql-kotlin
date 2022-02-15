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

import com.expediagroup.graphql.client.serialization.types.OptionalInput
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OptionalScalarListSerializerTest {

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
            optionalBooleanList = OptionalInput.Defined(null),
            optionalDoubleList = OptionalInput.Defined(null),
            optionalIntList = OptionalInput.Defined(null),
            optionalStringList = OptionalInput.Defined(null)
        )
        assertEquals("""{"optionalBooleanList":null,"optionalDoubleList":null,"optionalIntList":null,"optionalStringList":null}""", json.encodeToString(explicitNull))
    }

    @Test
    fun `verify defined values are serialized correctly`() {
        val defined = InputWrapper(
            optionalBooleanList = OptionalInput.Defined(listOf(true, false)),
            optionalDoubleList = OptionalInput.Defined(listOf(1.23, 2.34)),
            optionalIntList = OptionalInput.Defined(listOf(123, 234)),
            optionalStringList = OptionalInput.Defined(listOf("foo", "bar"))
        )
        assertEquals("""{"optionalBooleanList":[true,false],"optionalDoubleList":[1.23,2.34],"optionalIntList":[123,234],"optionalStringList":["foo","bar"]}""", json.encodeToString(defined))
    }

    @Serializable
    data class InputWrapper(
        @Serializable(with = OptionalScalarListSerializer::class)
        val optionalBooleanList: OptionalInput<List<Boolean>> = OptionalInput.Undefined,
        @Serializable(with = OptionalScalarListSerializer::class)
        val optionalDoubleList: OptionalInput<List<Double>> = OptionalInput.Undefined,
        @Serializable(with = OptionalScalarListSerializer::class)
        val optionalIntList: OptionalInput<List<Int>> = OptionalInput.Undefined,
        @Serializable(with = OptionalScalarListSerializer::class)
        val optionalStringList: OptionalInput<List<String>> = OptionalInput.Undefined
    )
}
