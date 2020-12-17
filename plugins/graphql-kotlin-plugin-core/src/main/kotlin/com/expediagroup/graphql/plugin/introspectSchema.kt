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

import com.expediagroup.graphql.plugin.config.TimeoutConfig
import graphql.introspection.IntrospectionResultToSchema
import graphql.schema.idl.SchemaPrinter
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.TimeoutCancellationException

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
@KtorExperimentalAPI
suspend fun introspectSchema(endpoint: String, httpHeaders: Map<String, Any> = emptyMap(), timeoutConfig: TimeoutConfig = TimeoutConfig()): String = HttpClient(engineFactory = CIO) {
    engine {
        requestTimeout = timeoutConfig.read
        endpoint {
            connectTimeout = timeoutConfig.connect
        }
    }
    install(feature = JsonFeature)
}.use { client ->
    val introspectionResult = try {
        client.post<Map<String, Any?>> {
            url(endpoint)
            contentType(ContentType.Application.Json)
            httpHeaders.forEach { (name, value) ->
                header(name, value)
            }
            body = mapOf(
                "query" to INTROSPECTION_QUERY,
                "operationName" to "IntrospectionQuery"
            )
        }
    } catch (e: Throwable) {
        when (e) {
            is ClientRequestException, is TimeoutCancellationException -> throw e
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
