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
import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_CLIENT_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.INTROSPECT_SCHEMA_TASK_NAME
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphQLGradlePluginIT : GraphQLGradlePluginAbstractIT() {

    @Test
    fun `apply the plugin extension to generate client with defaults`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val buildFileContents =
            """
            application {
              applicationDefaultJvmArgs = listOf("-DgraphQLEndpoint=${wireMockServer.baseUrl()}/graphql")
              mainClassName = "com.example.ApplicationKt"
            }

            graphql {
              client {
                endpoint = "${wireMockServer.baseUrl()}/graphql"
                packageName = "com.example.generated"
              }
            }
            """.trimIndent()
        testProjectDirectory.generateBuildFile(buildFileContents)
        testProjectDirectory.createTestFile("JUnitQuery.graphql", "src/main/resources")
            .writeText(testQuery)
        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("Application", mapOf("customScalarsEnabled" to false)))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments("build", "run")
            .build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$INTROSPECT_SCHEMA_TASK_NAME")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_CLIENT_TASK_NAME")?.outcome)
        assertTrue(File(testProjectDirectory, "build/schema.graphql").exists())
        assertTrue(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/JUnitQuery.kt").exists())
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":run")?.outcome)
    }

    @Test
    fun `apply the plugin extension to generate client`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val customHeaderName = "X-Custom-Header"
        val customHeaderValue = "My-Custom-Header-Value"
        WireMock.reset()
        WireMock.stubFor(stubSdlEndpoint().withHeader(customHeaderName, EqualToPattern(customHeaderValue)))
        WireMock.stubFor(stubGraphQLResponse())

        val buildFileContents =
            """
            application {
              applicationDefaultJvmArgs = listOf("-DgraphQLEndpoint=${wireMockServer.baseUrl()}/graphql")
              mainClassName = "com.example.ApplicationKt"
            }

            graphql {
              client {
                sdlEndpoint = "${wireMockServer.baseUrl()}/sdl"
                packageName = "com.example.generated"

                // optional
                allowDeprecatedFields = true
                headers["$customHeaderName"] = "$customHeaderValue"
                converters["UUID"] = ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter")
                queryFiles.add(file("${'$'}{project.projectDir}/src/main/resources/queries/JUnitQuery.graphql"))
                queryFiles.add(file("${'$'}{project.projectDir}/src/main/resources/queries/DeprecatedQuery.graphql"))
              }
            }
            """.trimIndent()
        testProjectDirectory.generateBuildFile(buildFileContents)
        testProjectDirectory.createTestFile("JUnitQuery.graphql", "src/main/resources/queries")
            .writeText(testQuery)
        testProjectDirectory.createTestFile("DeprecatedQuery.graphql", "src/main/resources/queries")
            .writeText(loadResource("mocks/DeprecatedQuery.graphql"))
        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("Application", mapOf("customScalarsEnabled" to true)))
        testProjectDirectory.createTestFile("UUIDScalarConverter.kt", "src/main/kotlin/com/example")
            .writeText(loadResource("mocks/UUIDScalarConverter.kt"))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments("build", "run")
            .build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$DOWNLOAD_SDL_TASK_NAME")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_CLIENT_TASK_NAME")?.outcome)
        assertTrue(File(testProjectDirectory, "build/schema.graphql").exists())
        assertTrue(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/JUnitQuery.kt").exists())
        assertTrue(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/DeprecatedQuery.kt").exists())
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":run")?.outcome)
    }
}
