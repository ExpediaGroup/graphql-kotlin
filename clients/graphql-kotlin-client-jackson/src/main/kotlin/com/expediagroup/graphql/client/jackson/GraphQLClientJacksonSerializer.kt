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

package com.expediagroup.graphql.client.jackson

import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLResponse
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.UndefinedFilter
import com.expediagroup.graphql.client.serializer.GraphQLClientSerializer
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.fasterxml.jackson.annotation.JsonInclude
import tools.jackson.databind.JavaType
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.cfg.EnumFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.jacksonMapperBuilder
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Jackson based GraphQL request/response serializer.
 */
class GraphQLClientJacksonSerializer(mapper: JsonMapper = jacksonMapperBuilder().build()) : GraphQLClientSerializer {
    private val mapper: ObjectMapper = configureMapper(mapper)
    private val typeCache = ConcurrentHashMap<KClass<*>, JavaType>()

    override fun serialize(request: GraphQLClientRequest<*>): String = mapper.writeValueAsString(request)

    override fun serialize(requests: List<GraphQLClientRequest<*>>): String = mapper.writeValueAsString(requests)

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
            // should never be the case
            listOf(mapper.convertValue(jsonResponse, parameterizedType(responseTypes.first())))
        }
    }

    private fun <T : Any> parameterizedType(resultType: KClass<T>): JavaType =
        typeCache.computeIfAbsent(resultType) {
            mapper.typeFactory.constructParametricType(JacksonGraphQLResponse::class.java, resultType.java)
        }

    companion object {
        private fun configureMapper(mapper: JsonMapper): JsonMapper = mapper.rebuild()
            .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .changeDefaultPropertyInclusion { it.withValueInclusion(JsonInclude.Include.NON_NULL) }
            .withConfigOverride(OptionalInput::class.java) { cfg ->
                cfg.setInclude(
                    JsonInclude.Value.empty()
                        .withValueInclusion(JsonInclude.Include.CUSTOM)
                        .withValueFilter(UndefinedFilter::class.java)
                )
            }
            .build()
    }
}
