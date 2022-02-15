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

package com.expediagroup.graphql.client.serialization.data.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.serialization.serializers.AnyKSerializer
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import java.util.UUID
import kotlin.Any
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.collections.mapOf
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Generated
object OptionalInputSerializer : KSerializer<OptionalInput<Any>> {
    private val delegates: Map<KClass<*>, KSerializer<*>> = mapOf(UUID::class to UUIDSerializer)

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OptionalInput")

    @Suppress("UNCHECKED_CAST")
    override fun serialize(encoder: Encoder, `value`: OptionalInput<Any>) {
        when (value) {
            is OptionalInput.Undefined -> {
                return
            }
            is OptionalInput.Defined<*> -> {
                val definedValue = value.value
                if (definedValue != null) {
                    if (definedValue is List<*>) {
                        val element = definedValue.firstOrNull()
                        val elementSerializer = if (element == null) {
                            AnyKSerializer
                        } else {
                            delegates[element::class] as? KSerializer<Any?> ?: AnyKSerializer
                        }
                        encoder.encodeSerializableValue(ListSerializer(elementSerializer), definedValue)
                    } else {
                        val delegate: KSerializer<Any?> = delegates[definedValue::class] as? KSerializer<Any?> ?: AnyKSerializer
                        encoder.encodeSerializableValue(delegate, definedValue)
                    }
                } else {
                    encoder.encodeNull()
                }
            }
        }
    }

    /**
     * undefined is only supported during client serialization, this code should never be invoked
     */
    override fun deserialize(decoder: Decoder): OptionalInput<Any> =
        OptionalInput.Defined(AnyKSerializer.deserialize(decoder))
}
