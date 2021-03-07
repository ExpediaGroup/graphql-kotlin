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

package com.expediagroup.graphql.client.serialization.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

/**
 * Custom KSerializer that can serialize/deserialize GraphQL error paths.
 *
 * GraphQL error can contain execution path that represents path of the the response field that encountered the error. Path segments represent either a field
 * name (String) or 0-based integer which is a list index.
 *
 * NOTE: we need separate serializer as generic [AnyKSerializer] should be able to process nullable fields (i.e. `Any?`). GraphQL error path is a non-nullable
 * list consisting of Strings and Integers.
 */
class GraphQLErrorPathSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GraphQLErrorPath")

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as JsonEncoder
        val jsonElement = when (value) {
            is Int -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            else -> {
                // should never be the case
                JsonPrimitive(value.toString())
            }
        }
        jsonEncoder.encodeJsonElement(jsonElement)
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement() as JsonPrimitive
        return if (!element.isString) {
            element.content.toIntOrNull() ?: element.content
        } else {
            element.content
        }
    }
}
