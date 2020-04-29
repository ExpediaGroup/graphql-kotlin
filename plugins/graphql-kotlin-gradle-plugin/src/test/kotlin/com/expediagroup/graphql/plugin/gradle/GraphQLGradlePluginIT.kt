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

package com.expediagroup.graphql.plugin.gradle

import com.expediagroup.graphql.plugin.gradle.tasks.DOWNLOAD_SDL_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.INTROSPECT_SCHEMA_TASK_NAME
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.ContainsPattern
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.BufferedReader
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphQLGradlePluginIT {

    private val expectedSchema = ClassLoader.getSystemClassLoader().getResourceAsStream("testSchema.graphql")?.use {
        BufferedReader(it.reader()).readText()
    }
    private val introspectionResult = ClassLoader.getSystemClassLoader().getResourceAsStream("introspectionResult.json")?.use {
        BufferedReader(it.reader()).readText()
    } ?: throw RuntimeException("failure setting up test environment - unable to load introspectionResult.json")

    @BeforeEach
    fun setUp() {
        WireMock.reset()
        WireMock.stubFor(WireMock.get("/sdl")
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/plain")
                .withBody(expectedSchema)))
        WireMock.stubFor(WireMock.post("/graphql")
            .withRequestBody(ContainsPattern("IntrospectionQuery"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(introspectionResult)))
    }

    @Test
    fun `apply the gradle plugin and execute downloadSDL task`(@TempDir tempDir: Path) {
        val buildFileContents = """
            import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask

            plugins {
              id("com.expediagroup.graphql")
            }

            val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
              endpoint.set("${wireMockServer.baseUrl()}/sdl")
            }
        """.trimIndent()

        File(tempDir.toFile(), "build.gradle.kts")
            .writeText(buildFileContents)

        val result = GradleRunner.create()
            .withProjectDir(tempDir.toFile())
            .withPluginClasspath()
            .withArguments(DOWNLOAD_SDL_TASK_NAME)
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":$DOWNLOAD_SDL_TASK_NAME")?.outcome)
        assertTrue(File(tempDir.toFile(), "build/schema.graphql").exists())
    }

    @Test
    fun `apply the gradle plugin and execute introspectSchema task`(@TempDir tempDir: Path) {
        val buildFileContents = """
            import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask

            plugins {
              id("com.expediagroup.graphql")
            }

            val graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {
              endpoint.set("${wireMockServer.baseUrl()}/graphql")
            }
        """.trimIndent()

        File(tempDir.toFile(), "build.gradle.kts")
            .writeText(buildFileContents)

        val result = GradleRunner.create()
            .withProjectDir(tempDir.toFile())
            .withPluginClasspath()
            .withArguments(INTROSPECT_SCHEMA_TASK_NAME)
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":$INTROSPECT_SCHEMA_TASK_NAME")?.outcome)
        assertTrue(File(tempDir.toFile(), "build/schema.graphql").exists())
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
