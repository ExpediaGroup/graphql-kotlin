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

package com.expediagroup.graphql.client.spring

import com.expediagroup.graphql.client.jackson.GraphQLClientJacksonSerializer
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLError
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLResponse
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLSourceLocation
import com.expediagroup.graphql.client.serialization.GraphQLClientKotlinxSerializer
import com.expediagroup.graphql.client.serialization.serializers.AnyKSerializer
import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLError
import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLResponse
import com.expediagroup.graphql.client.serialization.types.KotlinxGraphQLSourceLocation
import com.expediagroup.graphql.client.types.AutomaticPersistedQueriesSettings
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.netty.http.client.HttpClient
import java.time.Duration
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GraphQLWebClientTest {

    private val json = Json
    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        WireMock.reset()
    }

    @Test
    fun `verifies spring web client can execute query using jackson`() {
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

        val client = GraphQLWebClient(
            url = "${wireMockServer.baseUrl()}/graphql",
            serializer = GraphQLClientJacksonSerializer()
        )
        runBlocking {
            val result: GraphQLClientResponse<HelloWorldResult> = client.execute(HelloWorldQuery())

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
    fun `verifies spring web client can execute query using kotlinx serialization`() {
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

        val client = GraphQLWebClient(
            url = "${wireMockServer.baseUrl()}/graphql",
            serializer = GraphQLClientKotlinxSerializer()
        )
        runBlocking {
            val result: GraphQLClientResponse<HelloWorldResult> = client.execute(HelloWorldQuery())
            verifyResponse(expectedResponse, result)
        }
    }

    @Test
    fun `verifies spring web client can execute batch requests using kotlinx serialization`() {
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

        val client = GraphQLWebClient(
            url = "${wireMockServer.baseUrl()}/graphql",
            serializer = GraphQLClientKotlinxSerializer()
        )
        runBlocking {
            val result: List<GraphQLClientResponse<*>> = client.execute(listOf(HelloWorldQuery(), GoodbyeWorldQuery()))
            verifyResponses(expectedResponse, result)
        }
    }

    @Test
    fun `verifies spring web client can execute batch requests using jackson`() {
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

        val client = GraphQLWebClient(
            url = "${wireMockServer.baseUrl()}/graphql",
            serializer = GraphQLClientJacksonSerializer()
        )
        runBlocking {
            val result: List<GraphQLClientResponse<*>> = client.execute(listOf(HelloWorldQuery(), GoodbyeWorldQuery()))
            verifyResponses(expectedResponse, result)
        }
    }

    @Test
    fun `verifies spring web client instance can be customized`() {
        val expectedResponse = JacksonGraphQLResponse(data = HelloWorldResult("Hello World!"))
        WireMock.stubFor(stubJacksonResponse(response = expectedResponse, delayMillis = 500))

        val httpClient: HttpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10)
            .responseTimeout(Duration.ofMillis(10))
        val connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient.wiretap(true))
        val webClientBuilder = WebClient.builder()
            .clientConnector(connector)

        val client = GraphQLWebClient(
            url = "${wireMockServer.baseUrl()}/graphql",
            builder = webClientBuilder,
            serializer = GraphQLClientJacksonSerializer()
        )
        runBlocking {
            val exception = assertFailsWith(WebClientRequestException::class) {
                client.execute(HelloWorldQuery())
            }
            assertTrue(exception.cause is ReadTimeoutException)
        }
    }

    @Test
    fun `verifies individual spring web client requests can be customized`() {
        val expectedResponse = JacksonGraphQLResponse(data = HelloWorldResult("Hello World!"))
        val customHeaderName = "X-Custom-Header"
        val customHeaderValue = "My-Custom-Header-Value"
        WireMock.stubFor(stubJacksonResponse(expectedResponse).withHeader(customHeaderName, EqualToPattern(customHeaderValue)))

        val client = GraphQLWebClient(
            url = "${wireMockServer.baseUrl()}/graphql",
            serializer = GraphQLClientJacksonSerializer()
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
    fun `verifies spring web client can execute query using GET with persisted query`() {
        val expectedResponse = JacksonGraphQLResponse(data = HelloWorldResult("Hello World!"))
        WireMock.stubFor(
            WireMock
                .get("""/graphql?extension=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22sha256Hash%22%3A%22dd79d72356e3cfd09a542b572c3c73e4e8d90c1c7d5c27d74bcff4e7423178ae%22%7D%7D""")
                .withHeader("Content-Type", EqualToPattern("application/x-www-form-urlencoded"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedResponse))
                )
        )

        val client = GraphQLWebClient(
            url = "${wireMockServer.baseUrl()}/graphql",
            serializer = GraphQLClientJacksonSerializer(),
            automaticPersistedQueriesSettings = AutomaticPersistedQueriesSettings(enabled = true)
        )

        runBlocking {
            val result: GraphQLClientResponse<HelloWorldResult> = client.execute(HelloWorldQuery())

            assertNotNull(result)
            assertNotNull(result.data)
            assertEquals(expectedResponse.data?.helloWorld, result.data?.helloWorld)
            assertNull(result.errors)
            assertNull(result.extensions)
        }
    }

    @Test
    fun `verifies spring web client can execute query using POST with persisted query`() {
        val expectedResponse = JacksonGraphQLResponse(data = HelloWorldResult("Hello World!"))
        WireMock.stubFor(
            WireMock
                .post("/graphql")
                .withHeader("Content-Type", EqualToPattern("application/json"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedResponse))
                )
        )

        val client = GraphQLWebClient(
            url = "${wireMockServer.baseUrl()}/graphql",
            serializer = GraphQLClientJacksonSerializer(),
            automaticPersistedQueriesSettings = AutomaticPersistedQueriesSettings(enabled = true, httpMethod = AutomaticPersistedQueriesSettings.HttpMethod.POST)
        )

        runBlocking {
            val result: GraphQLClientResponse<HelloWorldResult> = client.execute(HelloWorldQuery())

            assertNotNull(result)
            assertNotNull(result.data)
            assertEquals(expectedResponse.data?.helloWorld, result.data?.helloWorld)
            assertNull(result.errors)
            assertNull(result.extensions)
        }
    }

    @Test
    fun `verifies spring web client can execute query using GET with not persisted query`() {
        val expectedErrorResponse = JacksonGraphQLResponse(
            data = null,
            errors = listOf(
                JacksonGraphQLError(
                    message = "PersistedQueryNotFound"
                )
            ),
            extensions = mapOf("persistedQueryId" to "dd79d72356e3cfd09a542b572c3c73e4e8d90c1c7d5c27d74bcff4e7423178ae", "classification" to "PersistedQueryNotFound")
        )
        WireMock.stubFor(
            WireMock
                .get("""/graphql?extension=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22sha256Hash%22%3A%22dd79d72356e3cfd09a542b572c3c73e4e8d90c1c7d5c27d74bcff4e7423178ae%22%7D%7D""")
                .withHeader("Content-Type", EqualToPattern("application/x-www-form-urlencoded"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedErrorResponse))
                )
        )

        val expectedResponse = JacksonGraphQLResponse(data = HelloWorldResult("Hello World!"))
        WireMock.stubFor(
            WireMock
                .get(
                    """
                        |/graphql?query=%7B%22query%22%3A%22query+HelloWorldQuery+%7B+helloWorld+%7D%22%2C%22operationName%22%3A%22HelloWorld%22%7D&
                        |extension=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22sha256Hash%22%3A%22dd79d72356e3cfd09a542b572c3c73e4e8d90c1c7d5c27d74bcff4e7423178ae%22%7D%7D
                    """.trimMargin().replace("\n", "")
                )
                .withHeader("Content-Type", EqualToPattern("application/x-www-form-urlencoded"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedResponse))
                )
        )

        val client = GraphQLWebClient(
            url = "${wireMockServer.baseUrl()}/graphql",
            serializer = GraphQLClientJacksonSerializer(),
            automaticPersistedQueriesSettings = AutomaticPersistedQueriesSettings(enabled = true)
        )

        runBlocking {
            val result: GraphQLClientResponse<HelloWorldResult> = client.execute(HelloWorldQuery())

            assertNotNull(result)
            assertNotNull(result.data)
            assertEquals(expectedResponse.data?.helloWorld, result.data?.helloWorld)
            assertNull(result.errors)
            assertNull(result.extensions)
        }
    }

    @Test
    fun `verifies spring web client can execute query using POST with not persisted query`() {
        val expectedErrorResponse = JacksonGraphQLResponse(
            data = null,
            errors = listOf(
                JacksonGraphQLError(
                    message = "PersistedQueryNotFound"
                )
            ),
            extensions = mapOf("persistedQueryId" to "dd79d72356e3cfd09a542b572c3c73e4e8d90c1c7d5c27d74bcff4e7423178ae", "classification" to "PersistedQueryNotFound")
        )
        WireMock.stubFor(
            WireMock
                .post("/graphql")
                .withHeader("Content-Type", EqualToPattern("application/json"))
                .withRequestBody(
                    EqualToJsonPattern(
                        """
                            {
                              "operationName":"HelloWorld",
                              "extensions": {
                                "persistedQuery": {
                                  "version": 1,
                                  "sha256Hash": "dd79d72356e3cfd09a542b572c3c73e4e8d90c1c7d5c27d74bcff4e7423178ae"
                                }
                              }
                            }
                        """.trimIndent(),
                        true,
                        true
                    )
                )
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedErrorResponse))
                )
        )

        val expectedResponse = JacksonGraphQLResponse(data = HelloWorldResult("Hello World!"))
        WireMock.stubFor(
            WireMock
                .post("/graphql")
                .withHeader("Content-Type", EqualToPattern("application/json"))
                .withRequestBody(
                    EqualToJsonPattern(
                        """
                            {
                              "query": "query HelloWorldQuery { helloWorld }",
                              "operationName":"HelloWorld",
                              "extensions": {
                                "persistedQuery": {
                                  "version": 1,
                                  "sha256Hash": "dd79d72356e3cfd09a542b572c3c73e4e8d90c1c7d5c27d74bcff4e7423178ae"
                                }
                              }
                            }
                        """.trimIndent(),
                        true,
                        true
                    )
                )
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedResponse))
                )
        )

        val client = GraphQLWebClient(
            url = "${wireMockServer.baseUrl()}/graphql",
            serializer = GraphQLClientJacksonSerializer(),
            automaticPersistedQueriesSettings = AutomaticPersistedQueriesSettings(enabled = true, httpMethod = AutomaticPersistedQueriesSettings.HttpMethod.POST)
        )

        runBlocking {
            val result: GraphQLClientResponse<HelloWorldResult> = client.execute(HelloWorldQuery())

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

        val client = GraphQLWebClient(
            url = "${wireMockServer.baseUrl()}/graphql",
            serializer = GraphQLClientJacksonSerializer()
        )
        val error = assertFailsWith<WebClientResponseException> {
            runBlocking {
                client.execute(HelloWorldQuery())
            }
        }
        assertEquals(500, error.statusCode.value())
        assertEquals("Internal server error", error.responseBodyAsString)
    }

    @Test
    fun `verifies response with empty body will throw error`() {
        WireMock.stubFor(
            WireMock.post("/graphql")
                .willReturn(WireMock.aResponse().withStatus(204))
        )

        val client = GraphQLWebClient(
            url = "${wireMockServer.baseUrl()}/graphql",
            serializer = GraphQLClientJacksonSerializer()
        )
        assertFailsWith<NoSuchElementException> {
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
