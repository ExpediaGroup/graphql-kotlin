package com.expediagroup.graphql.client.jackson

import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLResponse
import com.expediagroup.graphql.client.serializer.GraphQLClientSerializer
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class GraphQLClientJacksonSerializer(private val mapper: ObjectMapper = jacksonObjectMapper()) : GraphQLClientSerializer {
    private val typeCache = ConcurrentHashMap<KClass<*>, JavaType>()

    init {
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
    }

    override fun serialize(request: Any): String = mapper.writeValueAsString(request)

    override fun <T : Any> deserialize(rawResponse: String, responseType: KClass<T>): JacksonGraphQLResponse<T> =
        mapper.readValue(rawResponse, parameterizedType(responseType))

    override fun deserialize(rawResponses: String, responseTypes: List<KClass<*>>): List<JacksonGraphQLResponse<*>> {
        val jsonResponse = mapper.readTree(rawResponses)

        return if (jsonResponse.isArray) {
            jsonResponse.withIndex().map { (index, element) ->
                val singleResponse: JacksonGraphQLResponse<*> = mapper.convertValue(element, parameterizedType(responseTypes[index]))
                singleResponse
            }
        } else {
            listOf(mapper.convertValue(jsonResponse, parameterizedType(responseTypes.first())))
        }
    }

    private fun <T : Any> parameterizedType(resultType: KClass<T>): JavaType =
        typeCache.computeIfAbsent(resultType) {
            mapper.typeFactory.constructParametricType(JacksonGraphQLResponse::class.java, resultType.java)
        }
}
