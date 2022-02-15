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

class OptionalScalarSerializerTest {

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
            optionalBoolean = OptionalInput.Defined(null),
            optionalDouble = OptionalInput.Defined(null),
            optionalInt = OptionalInput.Defined(null),
            optionalString = OptionalInput.Defined(null)
        )
        assertEquals("""{"optionalBoolean":null,"optionalDouble":null,"optionalInt":null,"optionalString":null}""", json.encodeToString(explicitNull))
    }

    @Test
    fun `verify defined values are serialized correctly`() {
        val defined = InputWrapper(
            optionalBoolean = OptionalInput.Defined(true),
            optionalDouble = OptionalInput.Defined(1.23),
            optionalInt = OptionalInput.Defined(123),
            optionalString = OptionalInput.Defined("foo")
        )
        assertEquals("""{"optionalBoolean":true,"optionalDouble":1.23,"optionalInt":123,"optionalString":"foo"}""", json.encodeToString(defined))
    }

    @Serializable
    data class InputWrapper(
        @Serializable(with = OptionalScalarSerializer::class)
        val optionalBoolean: OptionalInput<Boolean> = OptionalInput.Undefined,
        @Serializable(with = OptionalScalarSerializer::class)
        val optionalDouble: OptionalInput<Double> = OptionalInput.Undefined,
        @Serializable(with = OptionalScalarSerializer::class)
        val optionalInt: OptionalInput<Int> = OptionalInput.Undefined,
        @Serializable(with = OptionalScalarSerializer::class)
        val optionalString: OptionalInput<String> = OptionalInput.Undefined
    )
}
