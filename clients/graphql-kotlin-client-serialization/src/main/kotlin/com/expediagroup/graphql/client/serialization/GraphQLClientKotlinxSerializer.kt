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

package com.expediagroup.graphql.client.serialization

import com.expediagroup.graphql.client.serialization.serializers.AnyKSerializer
import com.expediagroup.graphql.client.serialization.types.KotlinXGraphQLResponse
import com.expediagroup.graphql.client.serializer.GraphQLClientSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.serializer
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

/**
 * GraphQL client serializer that uses kotlinx.serialization for serializing requests and deserializing responses.
 */
class GraphQLClientKotlinxSerializer(private val jsonBuilder: JsonBuilder.() -> Unit = {}) : GraphQLClientSerializer {

    private val serializerCache = ConcurrentHashMap<KClass<*>, KSerializer<KotlinXGraphQLResponse<Any?>>>()

    private val json = Json {
        ignoreUnknownKeys = true
        apply(jsonBuilder)
        classDiscriminator = "__typename"
        coerceInputValues = true
        encodeDefaults = true
    }

    override fun serialize(request: Any): String = json.encodeToString(AnyKSerializer, request)

    override fun <T : Any> deserialize(rawResponse: String, responseType: KClass<T>): KotlinXGraphQLResponse<T> = json.decodeFromString(
        responseSerializer(responseType) as KSerializer<KotlinXGraphQLResponse<T>>,
        rawResponse
    )

    override fun deserialize(rawResponses: String, responseTypes: List<KClass<*>>): List<KotlinXGraphQLResponse<*>> {
        val jsonElement = json.parseToJsonElement(rawResponses)
        return if (jsonElement is JsonArray) {
            jsonElement.withIndex().map { (index, element) ->
                json.decodeFromJsonElement(responseSerializer(responseTypes[index]), element)
            }
        } else {
            // should never be the case
            listOf(
                json.decodeFromJsonElement(responseSerializer(responseTypes.first()), jsonElement)
            )
        }
    }

    private fun <T : Any> responseSerializer(resultType: KClass<T>): KSerializer<KotlinXGraphQLResponse<Any?>> =
        serializerCache.computeIfAbsent(resultType) {
            val resultTypeSerializer = serializer(resultType.createType())
            KotlinXGraphQLResponse.serializer(
                resultTypeSerializer
            )
        }
}
