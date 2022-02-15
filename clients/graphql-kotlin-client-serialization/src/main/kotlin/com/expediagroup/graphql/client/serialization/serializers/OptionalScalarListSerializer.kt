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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * KSerializer that can serialize/deserialize optional lists of scalar values.
 */
object OptionalScalarListSerializer : KSerializer<OptionalInput<List<Any?>>> {
    private val delegate: KSerializer<List<Any?>> = ListSerializer(AnyKSerializer)

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OptionalScalarList")

    override fun serialize(encoder: Encoder, value: OptionalInput<List<Any?>>) {
        when (value) {
            is OptionalInput.Undefined -> { return }
            is OptionalInput.Defined<List<Any?>> -> {
                encoder.encodeNullableSerializableValue(delegate, value.value)
            }
        }
    }

    // undefined is only supported during client serialization
    override fun deserialize(decoder: Decoder): OptionalInput<List<Any?>> = OptionalInput.Defined(
        decoder.decodeNullableSerializableValue(delegate.nullable)
    )
}
