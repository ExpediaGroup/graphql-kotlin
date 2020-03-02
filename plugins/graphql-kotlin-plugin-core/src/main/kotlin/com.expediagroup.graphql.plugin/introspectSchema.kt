package com.expediagroup.graphql.plugin

import graphql.introspection.IntrospectionQuery
import graphql.introspection.IntrospectionResultToSchema
import graphql.schema.idl.SchemaPrinter
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI
import java.io.File

@KtorExperimentalAPI
suspend fun runIntrospectionQuery(endpoint: String, outputFile: File) {
    HttpClient(engineFactory = CIO) {
        install(feature = JsonFeature)
    }.use { client ->
        val introspectionResult = try {
            client.post<Map<String, Any?>> {
                url(endpoint)
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                body = mapOf(
                    "query" to IntrospectionQuery.INTROSPECTION_QUERY,
                    "operationName" to "IntrospectionQuery"
                )
            }
        } catch (e: Error) {
            throw RuntimeException("Unable to run introspection query against the specified endpoint=$endpoint")
        }

        @Suppress("UNCHECKED_CAST")
        val graphQLDocument = IntrospectionResultToSchema().createSchemaDefinition(introspectionResult["data"] as? Map<String, Any?>)
        val options = SchemaPrinter.Options.defaultOptions()
            .includeScalarTypes(true)
            .includeExtendedScalarTypes(true)
            .includeSchemaDefinition(true)
            .includeDirectives(true)
        outputFile.writeText(SchemaPrinter(options).print(graphQLDocument))
    }
}
