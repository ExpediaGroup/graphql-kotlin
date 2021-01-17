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

package com.expediagroup.graphql.examples.server.ktor

import com.expediagroup.graphql.server.execution.GraphQLBatchRequest
import com.expediagroup.graphql.server.execution.GraphQLRequestParser
import com.expediagroup.graphql.server.execution.GraphQLServerRequest
import com.expediagroup.graphql.server.execution.GraphQLSingleRequest
import com.expediagroup.graphql.types.GraphQLRequest
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.request.ApplicationRequest
import io.ktor.request.receiveText
import java.io.IOException

/**
 * Custom logic for how Ktor parses the incoming [ApplicationRequest] into the [GraphQLServerRequest]
 */
class KtorGraphQLRequestParser(
    private val mapper: ObjectMapper
) : GraphQLRequestParser<ApplicationRequest> {

    private val graphQLBatchRequestTypeReference: TypeReference<List<GraphQLRequest>> = object: TypeReference<List<GraphQLRequest>>() {}

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun parseRequest(request: ApplicationRequest): GraphQLServerRequest<*> = try {
        val rawRequest = request.call.receiveText()
        val jsonNode = mapper.readTree(rawRequest)
        if (jsonNode.isArray) {
            GraphQLBatchRequest(mapper.convertValue(jsonNode, graphQLBatchRequestTypeReference))
        } else {
            GraphQLSingleRequest(mapper.treeToValue(jsonNode, GraphQLRequest::class.java))
        }
    } catch (e: IOException) {
        throw IOException("Unable to parse GraphQL payload.")
    }
}
