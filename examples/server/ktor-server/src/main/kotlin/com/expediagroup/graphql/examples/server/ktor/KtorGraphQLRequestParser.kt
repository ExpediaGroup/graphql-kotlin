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

import com.expediagroup.graphql.server.execution.GraphQLRequestParser
import com.expediagroup.graphql.types.GraphQLRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.request.ApplicationRequest
import io.ktor.request.receiveText
import java.io.IOException

/**
 * Custom logic for how Ktor parses the incoming [ApplicationRequest] into the [GraphQLRequest]
 */
class KtorGraphQLRequestParser(
    private val mapper: ObjectMapper
) : GraphQLRequestParser<ApplicationRequest> {

    override suspend fun parseRequest(request: ApplicationRequest): GraphQLRequest {
        return try {
            mapper.readValue(request.call.receiveText())
        } catch (e: IOException) {
            throw IOException("Unable to parse GraphQL payload.")
        }
    }
}
