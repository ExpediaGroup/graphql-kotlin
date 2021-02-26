package com.expediagroup.graphql.client.serialization

import com.expediagroup.graphql.client.serialization.types.AnyKSerializer
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

class GraphQLClientKotlinxSerializer(private val jsonBuilder: JsonBuilder.() -> Unit = {}) : GraphQLClientSerializer {

    private val serializerCache = ConcurrentHashMap<KClass<*>, KSerializer<KotlinXGraphQLResponse<Any?>>>()

    private val json = Json {
        ignoreUnknownKeys = true
        apply(jsonBuilder)
        encodeDefaults = true
        classDiscriminator = "__typename"
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
