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

package com.expediagroup.graphql.plugin.gradle.tasks

import com.expediagroup.graphql.plugin.gradle.GraphQLGradlePluginAbstractIT
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

class GraphQLIntrospectSchemaTaskIT : GraphQLGradlePluginAbstractIT() {

    @Test
    @Tag("kts")
    fun `verify introspectSchema task (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val buildFileContents =
            """
            |val graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {
            |  endpoint.set("${wireMockServer.baseUrl()}/graphql")
            |}
            """.trimMargin()
        testProjectDirectory.generateBuildFileForClient(buildFileContents)
        verifyIntrospectionTaskSuccess(testProjectDirectory)
    }

    @Test
    @Tag("kts")
    fun `verify introspectSchema task with headers (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val customHeaderName = "X-Custom-Header"
        val customHeaderValue = "My-Custom-Header-Value"
        WireMock.reset()
        WireMock.stubFor(stubIntrospectionResult().withHeader(customHeaderName, EqualToPattern(customHeaderValue)))
        val buildFileContents =
            """
            |val graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {
            |  endpoint.set("${wireMockServer.baseUrl()}/graphql")
            |  headers.put("$customHeaderName", "$customHeaderValue")
            |}
            """.trimMargin()
        testProjectDirectory.generateBuildFileForClient(buildFileContents)
        verifyIntrospectionTaskSuccess(testProjectDirectory)
    }

    @Test
    @Tag("kts")
    fun `verify introspectSchema with timeout (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        WireMock.reset()
        WireMock.stubFor(stubIntrospectionResult(delay = 10_000))

        val buildFileContents =
            """
            |val graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {
            |  endpoint.set("${wireMockServer.baseUrl()}/graphql")
            |  timeoutConfig.set(TimeoutConfiguration(connect = 100, read = 100))
            |}
            """.trimMargin()
        testProjectDirectory.generateBuildFileForClient(buildFileContents)
        verifyIntrospectionTaskTimeout(testProjectDirectory)
    }

    @Test
    @Tag("groovy")
    fun `verify introspectSchema task (groovy)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val buildFileContents =
            """
            |graphqlIntrospectSchema {
            |  endpoint = "${wireMockServer.baseUrl()}/graphql"
            |}
            """.trimMargin()
        testProjectDirectory.generateGroovyBuildFileForClient(buildFileContents)
        verifyIntrospectionTaskSuccess(testProjectDirectory)
    }

    @Test
    @Tag("groovy")
    fun `verify introspectSchema task with headers (groovy)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val customHeaderName = "X-Custom-Header"
        val customHeaderValue = "My-Custom-Header-Value"
        WireMock.reset()
        WireMock.stubFor(stubIntrospectionResult().withHeader(customHeaderName, EqualToPattern(customHeaderValue)))
        val buildFileContents =
            """
            |graphqlIntrospectSchema {
            |  endpoint = "${wireMockServer.baseUrl()}/graphql"
            |  headers["$customHeaderName"] = "$customHeaderValue"
            |}
            """.trimMargin()
        testProjectDirectory.generateGroovyBuildFileForClient(buildFileContents)
        verifyIntrospectionTaskSuccess(testProjectDirectory)
    }

    @Test
    @Tag("groovy")
    fun `verify introspectSchema with timeout (groovy)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        WireMock.reset()
        WireMock.stubFor(stubIntrospectionResult(delay = 10_000))

        val buildFileContents =
            """
            |graphqlIntrospectSchema {
            |  endpoint = "${wireMockServer.baseUrl()}/graphql"
            |  timeoutConfig = new com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration(100, 100)
            |}
            """.trimMargin()
        testProjectDirectory.generateGroovyBuildFileForClient(buildFileContents)
        verifyIntrospectionTaskTimeout(testProjectDirectory)
    }

    private fun verifyIntrospectionTaskSuccess(testProjectDirectory: File) {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(INTROSPECT_SCHEMA_TASK_NAME, "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":$INTROSPECT_SCHEMA_TASK_NAME")?.outcome)
        assertTrue(File(testProjectDirectory, "build/schema.graphql").exists())
    }

    private fun verifyIntrospectionTaskTimeout(testProjectDirectory: File) {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(INTROSPECT_SCHEMA_TASK_NAME, "--stacktrace")
            .buildAndFail()

        assertEquals(TaskOutcome.FAILED, result.task(":$INTROSPECT_SCHEMA_TASK_NAME")?.outcome)
        assertTrue(result.output.contains("Timed out waiting for 100 ms", ignoreCase = true))
    }
}
