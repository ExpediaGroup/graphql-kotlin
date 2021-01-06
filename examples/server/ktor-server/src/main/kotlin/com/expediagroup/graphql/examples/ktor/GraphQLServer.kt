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

package com.expediagroup.graphql.examples.ktor

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.examples.ktor.schema.BookQueryService
import com.expediagroup.graphql.examples.ktor.schema.CourseQueryService
import com.expediagroup.graphql.examples.ktor.schema.HelloQueryService
import com.expediagroup.graphql.examples.ktor.schema.LoginMutationService
import com.expediagroup.graphql.examples.ktor.schema.UniversityQueryService
import com.expediagroup.graphql.examples.ktor.schema.models.User
import com.expediagroup.graphql.execution.GraphQLContext
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.toSchema
import com.expediagroup.graphql.types.GraphQLRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import graphql.GraphQL
import io.ktor.application.ApplicationCall
import io.ktor.request.ApplicationRequest
import io.ktor.request.receiveText
import io.ktor.response.respond
import java.io.IOException

class GraphQLServer {

    private val config = SchemaGeneratorConfig(supportedPackages = listOf("com.expediagroup.graphql.examples.ktor"))
    private val queries = listOf(
        TopLevelObject(HelloQueryService()),
        TopLevelObject(BookQueryService()),
        TopLevelObject(CourseQueryService()),
        TopLevelObject(UniversityQueryService())
    )
    private val mutations = listOf(TopLevelObject(LoginMutationService()))
    private val graphQLSchema = toSchema(config, queries, mutations)
    private val graphQL = GraphQL.newGraphQL(graphQLSchema).build()
    private val mapper = jacksonObjectMapper()
    private val dataLoaderRegistryFactory = KtorDataLoaderRegistryFactory()
    private val requestHandler = GraphQLRequestHandler(graphQL, dataLoaderRegistryFactory)

    /**
     * Execute a query against the schema
     */
    suspend fun handle(applicationCall: ApplicationCall) {
        val graphQLRequest = getRequest(applicationCall.request)
        val context = getContext(applicationCall.request)

        // Execute the query against the schema
        val result = requestHandler.executeRequest(graphQLRequest, context)

        // write response as json
        applicationCall.response.call.respond(mapper.writeValueAsString(result))
    }

    /**
     * Parse the [GraphQLRequest] from the Ktor http request.
     */
    private suspend fun getRequest(request: ApplicationRequest): GraphQLRequest {
        return try {
            mapper.readValue(request.call.receiveText())
        } catch (e: IOException) {
            throw IOException("Unable to parse GraphQL payload.")
        }
    }

    /**
     * Build a [GraphQLContext] to be used during execution.
     * In this step we can read header and tokens to add more information about the request.
     */
    private fun getContext(request: ApplicationRequest): AuthorizedContext {
        val loggedInUser = User(
            email = "fake@site.com",
            firstName = "Someone",
            lastName = "You Don't know",
            universityId = 4
        )

        // Parse any headers from the Ktor request
        val customHeader: String? = request.headers["my-custom-header"]

        return AuthorizedContext(loggedInUser, customHeader = customHeader)
    }
}
