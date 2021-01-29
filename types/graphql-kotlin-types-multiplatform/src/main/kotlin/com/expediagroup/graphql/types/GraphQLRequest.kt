/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.types

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.JsonElement

/**
 * GraphQL request that follows the common GraphQL HTTP request format and works with serialization and deserialization.
 *
 * @see [GraphQL Over HTTP](https://graphql.org/learn/serving-over-http/#post-request) for additional details
 */
@Serializable
data class GraphQLRequest(
    val query: String,
    val operationName: String? = null,
    val variables: Map<String, JsonElement>? = null
)

class AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Any", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Any {
        // No way to decode as JsonElement and check what's the type
        return decoder.decodeString()
    }

    override fun serialize(encoder: Encoder, value: Any) {
        when (value) {
            is String -> encoder.encodeString(value)
            is Int -> encoder.encodeInt(value)
            // ...
        }
    }
}
//object ColorAsStringSerializer : KSerializer<Color> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: Color) {
//        val string = value.rgb.toString(16).padStart(6, '0')
//        encoder.encodeString(string)
//    }
//
//    override fun deserialize(decoder: Decoder): Color {
//        val string = decoder.decodeString()
//        return Color(string.toInt(16))
//    }
//}
