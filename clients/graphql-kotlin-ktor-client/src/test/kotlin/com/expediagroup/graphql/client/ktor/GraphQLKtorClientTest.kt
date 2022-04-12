/*
 * Copyright 2022 Expedia, Inc
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

import com.expediagroup.graphql.client.jackson.GraphQLClientJacksonSerializer
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLError
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLResponse
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLSourceLocation
import com.expediagroup.graphql.client.serialization.GraphQLClientKotlinxSerializer
import com.expediagroup.graphql.client.serialization.serializers.AnyKSerializer
import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLError
import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLResponse
import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLSourceLocation
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
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

    private val json = Json
    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        WireMock.reset()
    }

    @Test
    fun `verifies ktor client can execute queries using kotlinx serialization`() {
        val expectedResponse = KotlinxGraphQLResponse(
            data = HelloWorldResult("Hello World!"),
            errors = listOf(
                KotlinxGraphQLError(
                    message = "helloWorld is also throwing an exception",
                    locations = listOf(KotlinxGraphQLSourceLocation(1, 1)),
                    path = listOf("helloWorld"),
                    extensions = mapOf("exceptionExtensionKey" to "JunitCustomValue")
                )
            ),
            extensions = mapOf("extensionKey" to "JUnitValue")
        )
        WireMock.stubFor(stubKotlinxResponse(expectedResponse))

        val client = GraphQLKtorClient(
            url = URL("${wireMockServer.baseUrl()}/graphql"),
            serializer = GraphQLClientKotlinxSerializer()
        )
        runBlocking {
            val result: GraphQLClientResponse<HelloWorldResult> = client.execute(HelloWorldQuery())
            verifyResponse(expectedResponse, result)
        }
    }

    @Test
    fun `verifies ktor client can execute queries using jackson`() {
        val expectedResponse = JacksonGraphQLResponse(
            data = HelloWorldResult("Hello World!"),
            errors = listOf(
                JacksonGraphQLError(
                    message = "helloWorld is also throwing an exception",
                    locations = listOf(JacksonGraphQLSourceLocation(1, 1)),
                    path = listOf("helloWorld"),
                    extensions = mapOf("exceptionExtensionKey" to "JunitCustomValue")
                )
            ),
            extensions = mapOf("extensionKey" to "JUnitValue")
        )
        WireMock.stubFor(stubJacksonResponse(expectedResponse))

        val client = GraphQLKtorClient(
            url = URL("${wireMockServer.baseUrl()}/graphql"),
            serializer = GraphQLClientJacksonSerializer()
        )
        runBlocking {
            val result: GraphQLClientResponse<HelloWorldResult> = client.execute(HelloWorldQuery())
            verifyResponse(expectedResponse, result)
        }
    }

    @Test
    fun `verifies ktor client can execute batch requests using kotlinx serialization`() {
        val expectedResponse = listOf(
            KotlinxGraphQLResponse(
                data = HelloWorldResult("Hello World!"),
                errors = listOf(
                    KotlinxGraphQLError(
                        message = "helloWorld is also throwing an exception",
                        locations = listOf(KotlinxGraphQLSourceLocation(1, 1)),
                        path = listOf("helloWorld"),
                        extensions = mapOf("exceptionExtensionKey" to "JunitCustomValue")
                    )
                ),
                extensions = mapOf("extensionKey" to "JUnitValue")
            ),
            KotlinxGraphQLResponse(
                data = GoodbyeWorldResult("Goodbye World!")
            )
        )
        WireMock.stubFor(stubKotlinxResponses(expectedResponse))

        val client = GraphQLKtorClient(
            url = URL("${wireMockServer.baseUrl()}/graphql"),
            serializer = GraphQLClientKotlinxSerializer()
        )
        runBlocking {
            val result: List<GraphQLClientResponse<*>> = client.execute(listOf(HelloWorldQuery(), GoodbyeWorldQuery()))
            verifyResponses(expectedResponse, result)
        }
    }

    @Test
    fun `verifies ktor client can execute batch requests using jackson`() {
        val expectedResponse = listOf(
            JacksonGraphQLResponse(
                data = HelloWorldResult("Hello World!"),
                errors = listOf(
                    JacksonGraphQLError(
                        message = "helloWorld is also throwing an exception",
                        locations = listOf(JacksonGraphQLSourceLocation(1, 1)),
                        path = listOf("helloWorld"),
                        extensions = mapOf("exceptionExtensionKey" to "JunitCustomValue")
                    )
                ),
                extensions = mapOf("extensionKey" to "JUnitValue")
            ),
            JacksonGraphQLResponse(
                data = GoodbyeWorldResult("Goodbye World!")
            )
        )
        WireMock.stubFor(stubJacksonResponse(expectedResponse))

        val client = GraphQLKtorClient(
            url = URL("${wireMockServer.baseUrl()}/graphql"),
            serializer = GraphQLClientJacksonSerializer()
        )
        runBlocking {
            val result: List<GraphQLClientResponse<*>> = client.execute(listOf(HelloWorldQuery(), GoodbyeWorldQuery()))
            verifyResponses(expectedResponse, result)
        }
    }

    @Test
    fun `verifies ktor client instance can be customized`() {
        val expectedResponse = KotlinxGraphQLResponse(data = HelloWorldResult("Hello World!"))
        WireMock.stubFor(stubKotlinxResponse(response = expectedResponse, delayMillis = 50))

        val httpClient = HttpClient(engineFactory = OkHttp) {
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

        val client = GraphQLKtorClient(
            url = URL("${wireMockServer.baseUrl()}/graphql"),
            httpClient = httpClient,
            serializer = GraphQLClientKotlinxSerializer()
        )
        runBlocking {
            assertFailsWith(SocketTimeoutException::class) {
                client.execute(HelloWorldQuery())
            }
        }
    }

    @Test
    fun `verifies individual ktor client requests can be customized`() {
        val expectedResponse = KotlinxGraphQLResponse(data = HelloWorldResult("Hello World!"))
        val customHeaderName = "X-Custom-Header"
        val customHeaderValue = "My-Custom-Header-Value"
        WireMock.stubFor(stubKotlinxResponse(expectedResponse).withHeader(customHeaderName, EqualToPattern(customHeaderValue)))

        val client = GraphQLKtorClient(
            url = URL("${wireMockServer.baseUrl()}/graphql"),
            serializer = GraphQLClientKotlinxSerializer()
        )
        runBlocking {
            val result: GraphQLClientResponse<HelloWorldResult> = client.execute(HelloWorldQuery()) {
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

        val client = GraphQLKtorClient(
            url = URL("${wireMockServer.baseUrl()}/graphql"),
            serializer = GraphQLClientKotlinxSerializer()
        )
        val error = assertFailsWith<ServerResponseException> {
            runBlocking {
                client.execute(HelloWorldQuery())
            }
        }
        assertEquals(500, error.response.status.value)
        assertEquals("Internal server error", runBlocking { error.response.bodyAsText() })
    }

    @Test
    fun `verifies response with empty body will throw error`() {
        WireMock.stubFor(
            WireMock.post("/graphql")
                .willReturn(WireMock.aResponse().withStatus(204))
        )

        val client = GraphQLKtorClient(
            url = URL("${wireMockServer.baseUrl()}/graphql"),
            serializer = GraphQLClientKotlinxSerializer()
        )
        assertFailsWith<SerializationException> {
            runBlocking {
                client.execute(HelloWorldQuery())
            }
        }
    }

    private fun stubResponse(response: String, delayMillis: Int): MappingBuilder =
        WireMock.post("/graphql")
            .willReturn(
                WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(response)
                    .withFixedDelay(delayMillis)
            )

    private fun stubKotlinxResponse(response: KotlinxGraphQLResponse<HelloWorldResult>, delayMillis: Int = 0): MappingBuilder =
        stubResponse(json.encodeToString(response), delayMillis)

    private fun stubKotlinxResponses(responses: List<KotlinxGraphQLResponse<*>>, delayMillis: Int = 0): MappingBuilder =
        stubResponse(json.encodeToString(AnyKSerializer, responses), delayMillis)

    private fun stubJacksonResponse(response: Any, delayMillis: Int = 0): MappingBuilder =
        stubResponse(objectMapper.writeValueAsString(response), delayMillis)

    private fun verifyResponse(expected: GraphQLClientResponse<*>, result: GraphQLClientResponse<*>) {
        assertNotNull(result.data)
        assertEquals(expected.data, result.data)
        assertEquals(expected.errors, result.errors)
        assertEquals(expected.extensions, result.extensions)
    }

    private fun verifyResponses(expectedResponses: List<GraphQLClientResponse<*>>, results: List<GraphQLClientResponse<*>>) {
        assertEquals(expectedResponses.size, results.size)
        for ((index, expected) in expectedResponses.withIndex()) {
            val result = results[index]
            verifyResponse(expected, result)
        }
    }

    @Serializable
    data class HelloWorldResult(val helloWorld: String)

    @Serializable
    class HelloWorldQuery : GraphQLClientRequest<HelloWorldResult> {
        override val query: String = "query HelloWorldQuery { helloWorld }"
        override val operationName: String = "HelloWorld"
        override fun responseType(): KClass<HelloWorldResult> = HelloWorldResult::class
    }

    @Serializable
    data class GoodbyeWorldResult(val goodbyeWorld: String)

    @Serializable
    class GoodbyeWorldQuery : GraphQLClientRequest<GoodbyeWorldResult> {
        override val query: String = "query GoodbyeWorldQuery { goodbyeWorld }"
        override val operationName: String = "GoodbyeWorld"
        override fun responseType(): KClass<GoodbyeWorldResult> = GoodbyeWorldResult::class
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
