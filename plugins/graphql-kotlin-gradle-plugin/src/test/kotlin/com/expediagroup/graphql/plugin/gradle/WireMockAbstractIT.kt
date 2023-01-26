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

package com.expediagroup.graphql.plugin.gradle

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.ContainsPattern
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import java.io.BufferedReader

abstract class WireMockAbstractIT {

    private val testSchema = loadResource("mocks/schema.graphql")
    private val introspectionResult = loadResource("mocks/IntrospectionResult.json")

    @BeforeEach
    fun setUp() {
        WireMock.reset()
        WireMock.stubFor(stubSdlEndpoint())
        WireMock.stubFor(stubIntrospectionResult())
    }

    fun stubSdlEndpoint(delay: Int? = null): MappingBuilder = WireMock.get("/sdl")
        .withResponse(content = testSchema, contentType = "text/plain", delay = delay)

    fun stubIntrospectionResult(delay: Int? = null): MappingBuilder = WireMock.post("/graphql")
        .withRequestBody(ContainsPattern("IntrospectionQuery"))
        .withResponse(content = introspectionResult, delay = delay)

    fun stubGraphQLResponse(queryName: String, content: String, delay: Int? = null): MappingBuilder = WireMock.post("/graphql")
        .withRequestBody(ContainsPattern(queryName))
        .withResponse(content = content, delay = delay)

    private fun MappingBuilder.withResponse(content: String, contentType: String = "application/json", delay: Int? = null) = this.willReturn(
        WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", contentType)
            .withBody(content)
            .withFixedDelay(delay ?: 0)
    )

    fun loadResource(resourceName: String) = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName)?.use {
        BufferedReader(it.reader()).readText()
    } ?: throw RuntimeException("unable to load $resourceName")

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
