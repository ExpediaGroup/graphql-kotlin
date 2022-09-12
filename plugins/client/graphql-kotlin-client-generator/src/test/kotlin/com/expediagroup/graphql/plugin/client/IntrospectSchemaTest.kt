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

package com.expediagroup.graphql.plugin.client

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.BufferedReader
import java.net.UnknownHostException
import kotlin.test.assertEquals

class IntrospectSchemaTest {

    @BeforeEach
    fun setUp() {
        WireMock.reset()
    }

    @Test
    fun `verify can run introspection query and generate valid schema`() {
        // loading schema from introspection results ends up with old deprecated description -> https://github.com/graphql-java/graphql-java/pull/2510
        val expectedSchema =
            """
                schema {
                  query: Query
                }

                "Marks the field, argument, input field or enum value as deprecated"
                directive @deprecated(
                    "The reason for the deprecation"
                    reason: String = "No longer supported"
                  ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

                "Directs the executor to include this field or fragment only when the `if` argument is true"
                directive @include(
                    "Included when true."
                    if: Boolean!
                  ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

                "Directs the executor to skip this field or fragment when the `if` argument is true."
                directive @skip(
                    "Skipped when true."
                    if: Boolean!
                  ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

                "Exposes a URL that specifies the behaviour of this scalar."
                directive @specifiedBy(
                    "The URL that specifies the behaviour of this scalar."
                    url: String!
                  ) on SCALAR

                type Query {
                  widget: Widget!
                }

                "Simple Widget"
                type Widget {
                  "Unique identifier"
                  id: Int!
                  "Name of the widget"
                  name: String!
                }
            """.trimIndent()
        val introspectionResult = ClassLoader.getSystemClassLoader()
            .getResourceAsStream("introspectionResult.json")
            ?.use {
                BufferedReader(it.reader()).readText()
            }
        WireMock.stubFor(
            WireMock.post("/graphql")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(introspectionResult)
                )
        )

        runBlocking {
            val sdl = introspectSchema("${wireMockServer.baseUrl()}/graphql")
            assertEquals(expectedSchema, sdl.trim())
        }
    }

    @Test
    fun `verify introspectSchema will throw exception if unable to run query`() {
        WireMock.stubFor(
            WireMock.post("/graphql")
                .willReturn(WireMock.aResponse().withStatus(404))
        )
        assertThrows<ClientRequestException> {
            runBlocking {
                introspectSchema("${wireMockServer.baseUrl()}/graphql")
            }
        }
    }

    @Test
    fun `verify introspectSchema will respect timeout setting`() {
        WireMock.stubFor(
            WireMock.post("/graphql")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withFixedDelay(1_000)
                )
        )
        assertThrows<HttpRequestTimeoutException> {
            runBlocking {
                introspectSchema(endpoint = "${wireMockServer.baseUrl()}/graphql", connectTimeout = 100, readTimeout = 100)
            }
        }
    }

    @Test
    fun `verify introspectSchema will throw exception if URL is not valid`() {
        assertThrows<UnknownHostException> {
            runBlocking {
                downloadSchema("https://non-existent-graphql-url.com/should_404")
            }
        }
    }

    companion object {
        private val wireMockServer: WireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())

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
