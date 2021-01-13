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

import com.expediagroup.graphql.client.execute
import com.expediagroup.graphql.types.GraphQLError
import com.expediagroup.graphql.types.GraphQLResponse
import com.expediagroup.graphql.types.SourceLocation
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("EXPERIMENTAL_API_USAGE")
class GraphQLKtorClientTest {

    private val jacksonObjectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        WireMock.reset()
    }

    @Test
    fun `verifies ktor client can retrieve data`() {
        val expectedResponse = GraphQLResponse<HelloWorldResult>(
            data = HelloWorldResult("Hello World!"),
            errors = listOf(
                GraphQLError(
                    message = "helloWorld is also throwing an exception",
                    locations = listOf(SourceLocation(1, 1)),
                    path = listOf("helloWorld"),
                    extensions = mapOf("exceptionExtensionKey" to "JunitCustomValue")
                )
            ),
            extensions = mapOf("extensionKey" to "JUnitValue")
        )
        WireMock.stubFor(stubResponse(expectedResponse))

        val client = GraphQLKtorClient(URL("${wireMockServer.baseUrl()}/graphql"))
        runBlocking {
            val result: GraphQLResponse<HelloWorldResult> = client.execute(
                query = "query HelloWorldQuery { helloWorld }",
                operationName = "HelloWorldQuery"
            )

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
        val expectedResponse = GraphQLResponse(data = HelloWorldResult("Hello World!"))
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
                client.execute<GraphQLResponse<HelloWorldResult>>(
                    query = "query HelloWorldQuery { helloWorld }",
                    operationName = "HelloWorldQuery"
                )
            }
        }
    }

    @Test
    fun `verifies individual ktor client requests can be customized`() {
        val expectedResponse = GraphQLResponse(data = HelloWorldResult("Hello World!"))
        val customHeaderName = "X-Custom-Header"
        val customHeaderValue = "My-Custom-Header-Value"
        WireMock.stubFor(stubResponse(expectedResponse).withHeader(customHeaderName, EqualToPattern(customHeaderValue)))

        val client = GraphQLKtorClient(URL("${wireMockServer.baseUrl()}/graphql"))
        runBlocking {
            val result: GraphQLResponse<HelloWorldResult> = client.execute(
                query = "query HelloWorldQuery { helloWorld }",
                operationName = "HelloWorldQuery"
            ) {
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
                client.execute<HelloWorldResult>("query HelloWorldQuery { helloWorld }")
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
        assertFailsWith<MismatchedInputException> {
            runBlocking {
                client.execute<HelloWorldResult>("query HelloWorldQuery { helloWorld }")
            }
        }
    }

    private fun stubResponse(response: GraphQLResponse<*>, delayMillis: Int = 0): MappingBuilder =
        WireMock.post("/graphql")
            .willReturn(
                WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jacksonObjectMapper.writeValueAsString(response))
                    .withFixedDelay(delayMillis)
            )

    data class HelloWorldResult(val helloWorld: String)

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
