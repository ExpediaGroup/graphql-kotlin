package com.expediagroup.ktor.jackson

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.scalars.IDValueUnboxer
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.execution.GraphQLRequestParser
import com.expediagroup.graphql.server.execution.GraphQLServer
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import com.expediagroup.ktor.jackson.queries.HelloWorld
import com.expediagroup.ktor.jackson.queries.ObjectQuery
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.GraphQL
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.receiveText
import java.io.IOException

class KtorGraphQLServer(
    requestParser: GraphQLRequestParser<ApplicationRequest>,
    contextFactory: GraphQLContextFactory<ApplicationRequest>,
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
            val contextFactory = object: GraphQLContextFactory<ApplicationRequest> {}

            val config = SchemaGeneratorConfig(
                supportedPackages = listOf("com.expediagroup.ktor.jackson"),
            )
            val graphQLSchema = toSchema(config, listOf(
                TopLevelObject(HelloWorld()),
                TopLevelObject(ObjectQuery()),
            ))
            val graphQL: GraphQL = GraphQL.newGraphQL(graphQLSchema)
                .valueUnboxer(IDValueUnboxer())
                .build()
            val requestHandler = GraphQLRequestHandler(graphQL)

            return KtorGraphQLServer(requestParser, contextFactory, requestHandler)
        }
    }
}
