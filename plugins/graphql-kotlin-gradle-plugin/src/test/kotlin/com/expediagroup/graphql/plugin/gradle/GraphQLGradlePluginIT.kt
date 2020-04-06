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

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
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
    }
    private val testQuery = ClassLoader.getSystemClassLoader().getResourceAsStream("testQuery.graphql")?.use {
        BufferedReader(it.reader()).readText()
    } ?: throw RuntimeException("integration test was unable to load test query file")

    @BeforeEach
    fun setUp() {
        WireMock.reset()
        WireMock.stubFor(WireMock.get("/sdl")
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/plain")
                .withBody(expectedSchema)))
        WireMock.stubFor(WireMock.post("/graphql")
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(introspectionResult)))
    }

    @Test
    fun `apply the gradle plugin and execute downloadSDL task`(@TempDir tempDir: Path) {
        val buildFileContents = """
            import com.expediagroup.graphql.plugin.gradle.tasks.DownloadSDLTask

            plugins {
              id("com.expediagroup.graphql")
            }

            val downloadSDL by tasks.getting(DownloadSDLTask::class) {
              endpoint.set("${wireMockServer.baseUrl()}/sdl")
            }
        """.trimIndent()

        File(tempDir.toFile(),"build.gradle.kts")
            .writeText(buildFileContents)

        val result = GradleRunner.create()
            .withProjectDir(tempDir.toFile())
            .withPluginClasspath()
            .withArguments("downloadSDL")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":downloadSDL")?.outcome)
        assertTrue(File(tempDir.toFile(), "build/schema.graphql").exists())
    }

    @Test
    fun `apply the gradle plugin and execute introspectSchema task`(@TempDir tempDir: Path) {
        val buildFileContents = """
            import com.expediagroup.graphql.plugin.gradle.tasks.IntrospectSchemaTask

            plugins {
              id("com.expediagroup.graphql")
            }

            val introspectSchema by tasks.getting(IntrospectSchemaTask::class) {
              endpoint.set("${wireMockServer.baseUrl()}/graphql")
            }
        """.trimIndent()

        File(tempDir.toFile(),"build.gradle.kts")
            .writeText(buildFileContents)

        val result = GradleRunner.create()
            .withProjectDir(tempDir.toFile())
            .withPluginClasspath()
            .withArguments("introspectSchema")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":introspectSchema")?.outcome)
        assertTrue(File(tempDir.toFile(), "build/schema.graphql").exists())
    }

    @Test
    fun `apply the gradle plugin and execute generateClient task`(@TempDir tempDir: Path) {
        val buildFileContents = """
            import com.expediagroup.graphql.plugin.gradle.tasks.GenerateClientTask
            import com.expediagroup.graphql.plugin.gradle.tasks.IntrospectSchemaTask

            plugins {
              id("com.expediagroup.graphql")
            }

            val introspectSchema by tasks.getting(IntrospectSchemaTask::class) {
              endpoint.set("${wireMockServer.baseUrl()}/graphql")
            }
            val generateClient by tasks.getting(GenerateClientTask::class) {
              packageName.set("com.expediagroup.graphql.generated")
              schemaFile.set(introspectSchema.outputFile)
              dependsOn("introspectSchema")
            }
        """.trimIndent()
        File(tempDir.toFile(),"build.gradle.kts")
            .writeText(buildFileContents)

        val resourcesDir = File(tempDir.toFile(), "src/main/resources")
        resourcesDir.mkdirs()

        File(resourcesDir, "JUnitQuery.graphql")
            .writeText(testQuery)

        val result = GradleRunner.create()
            .withProjectDir(tempDir.toFile())
            .withPluginClasspath()
            .withArguments("generateClient")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":introspectSchema")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":generateClient")?.outcome)
        assertTrue(File(tempDir.toFile(), "build/schema.graphql").exists())
        assertTrue(File(tempDir.toFile(), "build/generated/source/graphql/com/expediagroup/graphql/generated/JUnitQuery.kt").exists())
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
