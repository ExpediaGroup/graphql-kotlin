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
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import graphql.schema.idl.errors.SchemaProblem
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.UnknownHostException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DownloadSchemaTest {

    @BeforeEach
    fun setUp() {
        WireMock.reset()
    }

    @Test
    fun `verify can download SDL`() {
        val expectedSchema =
            """
                schema {
                  query: Query
                }

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

                "Marks the field, argument, input field or enum value as deprecated"
                directive @deprecated(
                    "The reason for the deprecation"
                    reason: String! = "No longer supported"
                  ) on FIELD_DEFINITION | ENUM_VALUE

                type Query {
                  widget: Widget!
                }

                type Widget {
                  id: Int!
                  name: String!
                }
            """.trimIndent()
        stubFor(
            get("/sdl").willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody(expectedSchema)
            )
        )

        runBlocking {
            val sdl = downloadSchema("${wireMockServer.baseUrl()}/sdl")
            assertEquals(expectedSchema, sdl)
        }
    }

    @Test
    fun `verify downloadSchema will throw exception if URL is not valid`() {
        assertThrows<UnknownHostException> {
            runBlocking {
                downloadSchema("https://non-existent-graphql-url.com/should_404")
            }
        }
    }

    @Test
    fun `verify downloadSchema will throw exception if downloaded SDL is not valid schema`() {
        stubFor(
            get("/whatever").willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/plain")
                    .withBody("some random body")
            )
        )
        val exception = assertThrows<RuntimeException> {
            runBlocking {
                downloadSchema(endpoint = "${wireMockServer.baseUrl()}/whatever")
            }
        }
        assertTrue(exception is SchemaProblem)
    }

    @Test
    fun `verify downloadSchema will throw exception if unable to download schema`() {
        stubFor(
            get("/sdl").willReturn(aResponse().withStatus(404))
        )
        assertThrows<ClientRequestException> {
            runBlocking {
                downloadSchema("${wireMockServer.baseUrl()}/sdl")
            }
        }
    }

    @Test
    fun `verify downloadSchema will respect timeout setting`() {
        stubFor(
            get("/sdl").willReturn(
                aResponse()
                    .withStatus(200)
                    .withFixedDelay(1_000)
            )
        )
        assertThrows<HttpRequestTimeoutException> {
            runBlocking {
                downloadSchema(endpoint = "${wireMockServer.baseUrl()}/sdl", connectTimeout = 100, readTimeout = 100)
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
