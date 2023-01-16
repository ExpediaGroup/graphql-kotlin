/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.plugin.client

import graphql.introspection.IntrospectionResultToSchema
import graphql.schema.idl.SchemaPrinter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.runBlocking
import java.net.UnknownHostException

private const val INTROSPECTION_QUERY =
    """
    query IntrospectionQuery {
      __schema {
        queryType { name }
        mutationType { name }
        subscriptionType { name }
        types {
          ...FullType
        }
        directives {
          name
          description
          locations
          args {
            ...InputValue
          }
        }
      }
    }
    fragment FullType on __Type {
      kind
      name
      description
      fields(includeDeprecated: true) {
        name
        description
        args {
          ...InputValue
        }
        type {
          ...TypeRef
        }
        isDeprecated
        deprecationReason
      }
      inputFields {
        ...InputValue
      }
      interfaces {
        ...TypeRef
      }
      enumValues(includeDeprecated: true) {
        name
        description
        isDeprecated
        deprecationReason
      }
      possibleTypes {
        ...TypeRef
      }
    }
    fragment InputValue on __InputValue {
      name
      description
      type { ...TypeRef }
      defaultValue
    }
    fragment TypeRef on __Type {
      kind
      name
      ofType {
        kind
        name
        ofType {
          kind
          name
          ofType {
            kind
            name
            ofType {
              kind
              name
              ofType {
                kind
                name
                ofType {
                  kind
                  name
                  ofType {
                    kind
                    name
                  }
                }
              }
            }
          }
        }
      }
    }
    """

/**
 * Runs introspection query against specified GraphQL endpoint and returns underlying schema.
 */
fun introspectSchema(
    endpoint: String,
    httpHeaders: Map<String, Any> = emptyMap(),
    connectTimeout: Long = 5_000,
    readTimeout: Long = 15_000,
    streamResponse: Boolean = true
): String = HttpClient(engineFactory = Apache) {
    install(HttpTimeout) {
        connectTimeoutMillis = connectTimeout
        requestTimeoutMillis = readTimeout
    }
    install(ContentNegotiation) {
        jackson(streamRequestBody = streamResponse)
    }
}.use { client ->
    runBlocking {
        val introspectionResult = try {
            client.post {
                url(endpoint)
                contentType(ContentType.Application.Json)
                httpHeaders.forEach { (name, value) ->
                    header(name, value)
                }
                setBody(
                    mapOf(
                        "query" to INTROSPECTION_QUERY,
                        "operationName" to "IntrospectionQuery"
                    )
                )
                expectSuccess = true
            }.body<Map<String, Any?>>()
        } catch (e: Throwable) {
            when (e) {
                is ClientRequestException, is HttpRequestTimeoutException, is UnknownHostException -> throw e
                else -> throw RuntimeException("Unable to run introspection query against the specified endpoint=$endpoint", e)
            }
        }

        @Suppress("UNCHECKED_CAST")
        val graphQLDocument = IntrospectionResultToSchema().createSchemaDefinition(introspectionResult["data"] as? Map<String, Any?>)
        val options = SchemaPrinter.Options.defaultOptions()
            .includeScalarTypes(true)
            .includeSchemaDefinition(true)
            .includeDirectives(true)
        SchemaPrinter(options).print(graphQLDocument)
    }
}
