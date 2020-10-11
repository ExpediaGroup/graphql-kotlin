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
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphQLDownloadSDLTaskIT : GraphQLGradlePluginAbstractIT() {

    @Test
    @Tag("kts")
    fun `verify downloadSDL task (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val buildFileContents =
            """
            val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
              endpoint.set("${wireMockServer.baseUrl()}/sdl")
            }
            """.trimIndent()
        testProjectDirectory.generateBuildFile(buildFileContents)
        verifyDownloadSDLTaskSuccess(testProjectDirectory)
    }

    @Test
    @Tag("kts")
    fun `verify downloadSDL task with headers (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val customHeaderName = "X-Custom-Header"
        val customHeaderValue = "My-Custom-Header-Value"
        WireMock.reset()
        WireMock.stubFor(stubSdlEndpoint().withHeader(customHeaderName, EqualToPattern(customHeaderValue)))

        val buildFileContents =
            """
            val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
              endpoint.set("${wireMockServer.baseUrl()}/sdl")
              headers.put("$customHeaderName", "$customHeaderValue")
            }
            """.trimIndent()
        testProjectDirectory.generateBuildFile(buildFileContents)
        verifyDownloadSDLTaskSuccess(testProjectDirectory)
    }

    @Test
    @Tag("kts")
    fun `verify downloadSDL task with timeout (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        WireMock.reset()
        WireMock.stubFor(stubSdlEndpoint(delay = 10_000))

        val buildFileContents =
            """
            val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
              endpoint.set("${wireMockServer.baseUrl()}/sdl")
              timeoutConfig.set(TimeoutConfig(connect = 100, read = 100))
            }
            """.trimIndent()
        testProjectDirectory.generateBuildFile(buildFileContents)
        verifyDownloadSDLTaskTimeout(testProjectDirectory)
    }

    @Test
    @Tag("groovy")
    fun `verify downloadSDL task (groovy)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val buildFileContents =
            """
            graphqlDownloadSDL {
              endpoint = "${wireMockServer.baseUrl()}/sdl"
            }
            """.trimIndent()
        testProjectDirectory.generateGroovyBuildFile(buildFileContents)
        verifyDownloadSDLTaskSuccess(testProjectDirectory)
    }

    @Test
    @Tag("groovy")
    fun `verify downloadSDL task with headers (groovy)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val customHeaderName = "X-Custom-Header"
        val customHeaderValue = "My-Custom-Header-Value"
        WireMock.reset()
        WireMock.stubFor(stubSdlEndpoint().withHeader(customHeaderName, EqualToPattern(customHeaderValue)))

        val buildFileContents =
            """
            graphqlDownloadSDL {
                endpoint = "${wireMockServer.baseUrl()}/sdl"
                headers["$customHeaderName"] = "$customHeaderValue"
            }
            """.trimIndent()
        testProjectDirectory.generateGroovyBuildFile(buildFileContents)
        verifyDownloadSDLTaskSuccess(testProjectDirectory)
    }

    @Test
    @Tag("groovy")
    fun `verify downloadSDL task with timeout (groovy)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        WireMock.reset()
        WireMock.stubFor(stubSdlEndpoint(delay = 10_000))

        val buildFileContents =
            """
            graphqlDownloadSDL {
                endpoint = "${wireMockServer.baseUrl()}/sdl"
                timeoutConfig = new com.expediagroup.graphql.plugin.config.TimeoutConfig(100, 100)
            }
            """.trimIndent()
        testProjectDirectory.generateGroovyBuildFile(buildFileContents)
        verifyDownloadSDLTaskTimeout(testProjectDirectory)
    }

    private fun verifyDownloadSDLTaskSuccess(testProjectDirectory: File) {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(DOWNLOAD_SDL_TASK_NAME)
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":$DOWNLOAD_SDL_TASK_NAME")?.outcome)
        assertTrue(File(testProjectDirectory, "build/schema.graphql").exists())
    }

    private fun verifyDownloadSDLTaskTimeout(testProjectDirectory: File) {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(DOWNLOAD_SDL_TASK_NAME)
            .buildAndFail()

        assertEquals(TaskOutcome.FAILED, result.task(":$DOWNLOAD_SDL_TASK_NAME")?.outcome)
        assertTrue(result.output.contains("Timed out waiting for 100 ms", ignoreCase = true))
    }
}
