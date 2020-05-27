/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.plugin

import graphql.introspection.IntrospectionQuery
import graphql.introspection.IntrospectionResultToSchema
import graphql.schema.idl.SchemaPrinter
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI

/**
 * Runs introspection query against specified GraphQL endpoint and returns underlying schema.
 */
@KtorExperimentalAPI
suspend fun introspectSchema(endpoint: String, httpHeaders: Map<String, Any> = emptyMap()): String = HttpClient(engineFactory = CIO) {
    install(feature = JsonFeature)
}.use { client ->
    val introspectionResult = try {
        client.post<Map<String, Any?>> {
            url(endpoint)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            httpHeaders.forEach { (name, value) ->
                header(name, value)
            }
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
    SchemaPrinter(options).print(graphQLDocument)
}
