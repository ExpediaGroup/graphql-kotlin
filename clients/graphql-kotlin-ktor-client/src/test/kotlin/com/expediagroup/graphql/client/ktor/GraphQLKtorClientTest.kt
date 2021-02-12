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

package com.expediagroup.graphql.client.ktor

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.expediagroup.graphql.client.serialization.types.KotlinXGraphQLError
import com.expediagroup.graphql.client.serialization.types.KotlinXGraphQLResponse
import com.expediagroup.graphql.client.serialization.types.KotlinXSourceLocation
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.ServerResponseException
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.statement.readText
import io.ktor.network.sockets.SocketTimeoutException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("EXPERIMENTAL_API_USAGE")
class GraphQLKtorClientTest {

    private val json = Json { }

    @BeforeEach
    fun setUp() {
        WireMock.reset()
    }

    @Test
    fun `verifies ktor client can retrieve data`() {
        val expectedResponse = KotlinXGraphQLResponse(
            data = HelloWorldResult("Hello World!"),
            errors = listOf(
                KotlinXGraphQLError(
                    message = "helloWorld is also throwing an exception",
                    locations = listOf(KotlinXSourceLocation(1, 1)),
                    path = listOf("helloWorld"),
                    extensions = mapOf("exceptionExtensionKey" to "JunitCustomValue")
                )
            ),
            extensions = mapOf("extensionKey" to "JUnitValue")
        )
        WireMock.stubFor(stubResponse(expectedResponse))

        val client = GraphQLKtorClient(URL("${wireMockServer.baseUrl()}/graphql"))
        runBlocking {
            val result: GraphQLClientResponse<HelloWorldResult> = client.execute(HelloWorldRequest())

            assertNotNull(result)
            assertNotNull(result.data)
            assertEquals(expectedResponse.data?.helloWorld, result.data?.helloWorld)
            assertNotNull(result.errors)
            assertEquals(1, result.errors?.size)
            val expectedError = expectedResponse.errors?.first()!!
            val actualError = result.errors?.firstOrNull()
            assertNotNull(actualError)
            assertEquals(expectedError.message, actualError.message)
            assertEquals(1, actualError.locations?.size)
            assertEquals(expectedError.locations?.firstOrNull()?.column, actualError.locations?.firstOrNull()?.column)
            assertEquals(expectedError.locations?.firstOrNull()?.line, actualError.locations?.firstOrNull()?.line)
            assertEquals(1, actualError.path?.size)
            assertEquals("helloWorld", actualError.path?.firstOrNull())
            assertEquals("JunitCustomValue", actualError.extensions?.get("exceptionExtensionKey"))
            assertNotNull(result.extensions)
            assertEquals("JUnitValue", result.extensions?.get("extensionKey"))
        }
    }

    @Test
    fun `verifies ktor client instance can be customized`() {
        val expectedResponse = KotlinXGraphQLResponse(data = HelloWorldResult("Hello World!"))
        WireMock.stubFor(stubResponse(response = expectedResponse, delayMillis = 50))

        val client = GraphQLKtorClient(
            url = URL("${wireMockServer.baseUrl()}/graphql"),
            engineFactory = OkHttp
        ) {
            engine {
                config {
                    connectTimeout(10, TimeUnit.MILLISECONDS)
                    readTimeout(10, TimeUnit.MILLISECONDS)
                    writeTimeout(10, TimeUnit.MILLISECONDS)
                }
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }
        }
        runBlocking {
            assertFailsWith(SocketTimeoutException::class) {
                client.execute(HelloWorldRequest())
            }
        }
    }

    @Test
    fun `verifies individual ktor client requests can be customized`() {
        val expectedResponse = KotlinXGraphQLResponse(data = HelloWorldResult("Hello World!"))
        val customHeaderName = "X-Custom-Header"
        val customHeaderValue = "My-Custom-Header-Value"
        WireMock.stubFor(stubResponse(expectedResponse).withHeader(customHeaderName, EqualToPattern(customHeaderValue)))

        val client = GraphQLKtorClient(URL("${wireMockServer.baseUrl()}/graphql"))
        runBlocking {
            val result: GraphQLClientResponse<HelloWorldResult> = client.execute(HelloWorldRequest()) {
                header(customHeaderName, customHeaderValue)
            }

            assertNotNull(result)
            assertNotNull(result.data)
            assertEquals(expectedResponse.data?.helloWorld, result.data?.helloWorld)
            assertNull(result.errors)
            assertNull(result.extensions)
        }
    }

    @Test
    fun `verifies Non-OK HTTP responses will throw error`() {
        WireMock.stubFor(
            WireMock.post("/graphql")
                .willReturn(WireMock.aResponse().withStatus(500).withBody("Internal server error"))
        )

        val client = GraphQLKtorClient(URL("${wireMockServer.baseUrl()}/graphql"))
        val error = assertFailsWith<ServerResponseException> {
            runBlocking {
                client.execute(HelloWorldRequest())
            }
        }
        assertEquals(500, error.response.status.value)
        assertEquals("Internal server error", runBlocking { error.response.readText() })
    }

    @Test
    fun `verifies response with empty body will throw error`() {
        WireMock.stubFor(
            WireMock.post("/graphql")
                .willReturn(WireMock.aResponse().withStatus(204))
        )

        val client = GraphQLKtorClient(URL("${wireMockServer.baseUrl()}/graphql"))
        assertFailsWith<SerializationException> {
            runBlocking {
                client.execute(HelloWorldRequest())
            }
        }
    }

    private fun stubResponse(response: KotlinXGraphQLResponse<HelloWorldResult>, delayMillis: Int = 0): MappingBuilder =
        WireMock.post("/graphql")
            .willReturn(
                WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json.encodeToString(response))
                    .withFixedDelay(delayMillis)
            )

    @Serializable
    data class HelloWorldResult(val helloWorld: String)

    @Serializable
    class HelloWorldRequest : GraphQLClientRequest<HelloWorldResult> {
        override val query: String = "query HelloWorldQuery { helloWorld }"
        override val operationName: String = "HelloWorld"
        override fun responseType(): KClass<HelloWorldResult> = HelloWorldResult::class
    }

    companion object {
        internal val wireMockServer: WireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())

        @BeforeAll
        @JvmStatic
        fun oneTimeSetup() {
            wireMockServer.start()
            WireMock.configureFor(wireMockServer.port())
        }

        @AfterAll
        @JvmStatic
        fun oneTimeTearDown() {
            wireMockServer.stop()
        }
    }
}
