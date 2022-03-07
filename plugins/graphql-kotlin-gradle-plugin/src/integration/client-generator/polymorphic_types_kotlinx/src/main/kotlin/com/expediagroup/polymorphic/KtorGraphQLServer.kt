package com.expediagroup.polymorphic

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.execution.GraphQLContext
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.execution.GraphQLRequestParser
import com.expediagroup.graphql.server.execution.GraphQLServer
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import com.expediagroup.polymorphic.queries.PolymorphicQuery
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.GraphQL
import io.ktor.request.ApplicationRequest
import io.ktor.request.receiveText
import java.io.IOException

class KtorGraphQLServer(
    requestParser: GraphQLRequestParser<ApplicationRequest>,
    contextFactory: GraphQLContextFactory<GraphQLContext, ApplicationRequest>,
    requestHandler: GraphQLRequestHandler
) : GraphQLServer<ApplicationRequest>(requestParser, contextFactory, requestHandler) {

    companion object {
        operator fun invoke(jacksonObjectMapper: ObjectMapper): KtorGraphQLServer {
            val requestParser = object: GraphQLRequestParser<ApplicationRequest> {
                override suspend fun parseRequest(request: ApplicationRequest): GraphQLServerRequest = try {
                    val rawRequest = request.call.receiveText()
                    jacksonObjectMapper.readValue(rawRequest, GraphQLServerRequest::class.java)
                } catch (e: IOException) {
                    throw IOException("Unable to parse GraphQL payload.")
                }
            }
            val contextFactory = object: GraphQLContextFactory<GraphQLContext, ApplicationRequest> {}

            val config = SchemaGeneratorConfig(
                supportedPackages = listOf("com.expediagroup.polymorphic")
            )
            val graphQLSchema = toSchema(config, listOf(
                TopLevelObject(PolymorphicQuery())
            ))
            val graphQL: GraphQL = GraphQL.newGraphQL(graphQLSchema).build()
            val requestHandler = GraphQLRequestHandler(graphQL)

            return KtorGraphQLServer(requestParser, contextFactory, requestHandler)
        }
    }
}
