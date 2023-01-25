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

import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.expediagroup.graphql.plugin.gradle.tasks.DOWNLOAD_SDL_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_CLIENT_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.INTROSPECT_SCHEMA_TASK_NAME
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

class GraphQLGradlePluginIT : GraphQLGradlePluginAbstractIT() {

    @Test
    @Tag("kts")
    fun `apply the plugin extension to generate client with defaults (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val buildFileContents =
            """
            |application {
            |  applicationDefaultJvmArgs = listOf("-DgraphQLEndpoint=${wireMockServer.baseUrl()}/graphql")
            |  mainClassName = "com.example.ApplicationKt"
            |}
            |
            |graphql {
            |  client {
            |    endpoint = "${wireMockServer.baseUrl()}/graphql"
            |    packageName = "com.example.generated"
            |  }
            |}
            """.trimMargin()
        testProjectDirectory.generateBuildFileForClient(buildFileContents)
        testProjectDirectory.createTestFile("JUnitQuery.graphql", "src/main/resources")
            .writeText(testQuery)
        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("Application", mapOf("customScalarsEnabled" to false)))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments("build", "run", "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$INTROSPECT_SCHEMA_TASK_NAME")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_CLIENT_TASK_NAME")?.outcome)
        assertTrue(File(testProjectDirectory, "build/schema.graphql").exists())
        assertTrue(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/JUnitQuery.kt").exists())
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":run")?.outcome)
    }

    @Test
    @Tag("kts")
    fun `apply the plugin extension to generate client with local schema file (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val buildFileContents =
            """
            |application {
            |  applicationDefaultJvmArgs = listOf("-DgraphQLEndpoint=${wireMockServer.baseUrl()}/graphql")
            |  mainClassName = "com.example.ApplicationKt"
            |}
            |
            |graphql {
            |  client {
            |    schemaFile = file("${'$'}{project.projectDir}/src/main/resources/schema.graphql")
            |    packageName = "com.example.generated"
            |  }
            |}
            """.trimMargin()
        testProjectDirectory.generateBuildFileForClient(buildFileContents)
        testProjectDirectory.createTestFile("JUnitQuery.graphql", "src/main/resources")
            .writeText(testQuery)
        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("Application", mapOf("customScalarsEnabled" to false)))
        testProjectDirectory.createTestFile("schema.graphql", "src/main/resources")
            .writeText(testSchema)

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments("build", "run", "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_CLIENT_TASK_NAME")?.outcome)
        assertTrue(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/JUnitQuery.kt").exists())
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":run")?.outcome)
    }

    @Test
    @Tag("kts")
    fun `apply the plugin extension to generate client (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        // custom header to pass to SDL endpoint
        val customHeaderName = "X-Custom-Header"
        val customHeaderValue = "My-Custom-Header-Value"
        WireMock.reset()
        WireMock.stubFor(stubSdlEndpoint().withHeader(customHeaderName, EqualToPattern(customHeaderValue)))
        WireMock.stubFor(stubGraphQLResponse())

        val buildFileContents =
            """
            |application {
            |  applicationDefaultJvmArgs = listOf("-DgraphQLEndpoint=${wireMockServer.baseUrl()}/graphql")
            |  mainClassName = "com.example.ApplicationKt"
            |}
            |
            |graphql {
            |  client {
            |    sdlEndpoint = "${wireMockServer.baseUrl()}/sdl"
            |    packageName = "com.example.generated"
            |    // optional
            |    allowDeprecatedFields = true
            |    headers = mapOf("$customHeaderName" to "$customHeaderValue")
            |    customScalars = listOf(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))
            |    queryFiles = listOf(
            |        file("${'$'}{project.projectDir}/src/main/resources/queries/JUnitQuery.graphql"),
            |        file("${'$'}{project.projectDir}/src/main/resources/queries/DeprecatedQuery.graphql")
            |    )
            |  }
            |}
            """.trimMargin()
        testProjectDirectory.generateBuildFileForClient(buildFileContents)
        testProjectDirectory.createTestFile("JUnitQuery.graphql", "src/main/resources/queries")
            .writeText(testQuery)
        testProjectDirectory.createTestFile("DeprecatedQuery.graphql", "src/main/resources/queries")
            .writeText(loadResource("mocks/DeprecatedQuery.graphql"))
        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("Application", mapOf("customScalarsEnabled" to true)))
        testProjectDirectory.createTestFile("UUIDScalarConverter.kt", "src/main/kotlin/com/example")
            .writeText(loadResource("mocks/UUIDScalarConverter.txt"))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments("build", "run", "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$DOWNLOAD_SDL_TASK_NAME")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_CLIENT_TASK_NAME")?.outcome)
        assertTrue(File(testProjectDirectory, "build/schema.graphql").exists())
        assertTrue(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/JUnitQuery.kt").exists())
        assertTrue(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/DeprecatedQuery.kt").exists())
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":run")?.outcome)
    }

    @Test
    @Tag("kts")
    fun `apply the plugin extension to generate client using ktor and kotlinx serialization (kts)`(@TempDir tempDir: Path) {
        verifyCustomizedClient(tempDir.toFile(), true, GraphQLSerializer.KOTLINX)
    }

    @Test
    @Tag("kts")
    fun `apply the plugin extension to generate client using ktor and jackson (kts)`(@TempDir tempDir: Path) {
        verifyCustomizedClient(tempDir.toFile(), true, GraphQLSerializer.JACKSON)
    }

    @Test
    @Tag("kts")
    fun `apply the plugin extension to generate client using spring webclient and jackson (kts)`(@TempDir tempDir: Path) {
        verifyCustomizedClient(tempDir.toFile(), false, GraphQLSerializer.JACKSON)
    }

    @Test
    @Tag("kts")
    fun `apply the plugin extension to generate client using spring webclient and kotlinx serialization (kts)`(@TempDir tempDir: Path) {
        verifyCustomizedClient(tempDir.toFile(), false, GraphQLSerializer.KOTLINX)
    }

    private fun verifyCustomizedClient(testProjectDirectory: File, useKtorClient: Boolean = false, serializer: GraphQLSerializer = GraphQLSerializer.JACKSON) {
        // default global header
        val defaultHeaderName = "X-Default-Header"
        val defaultHeaderValue = "default"
        // custom header specified per request
        val customHeaderName = "X-Custom-Header"
        val customHeaderValue = "My-Custom-Header-Value"
        WireMock.reset()
        WireMock.stubFor(stubSdlEndpoint())
        WireMock.stubFor(
            stubGraphQLResponse()
                .withHeader(defaultHeaderName, EqualToPattern(defaultHeaderValue))
                .withHeader(customHeaderName, EqualToPattern(customHeaderValue))
        )

        val buildFileContents =
            """
            |application {
            |  applicationDefaultJvmArgs = listOf("-DgraphQLEndpoint=${wireMockServer.baseUrl()}/graphql")
            |  mainClassName = "com.example.ApplicationKt"
            |}
            |
            |graphql {
            |  client {
            |    sdlEndpoint = "${wireMockServer.baseUrl()}/sdl"
            |    packageName = "com.example.generated"
            |    serializer = GraphQLSerializer.$serializer
            |    useOptionalInputWrapper = true
            |  }
            |}
            """.trimMargin()
        val clientDependencies = when {
            useKtorClient && serializer == GraphQLSerializer.KOTLINX -> {
                "implementation(\"com.expediagroup:graphql-kotlin-ktor-client:$DEFAULT_PLUGIN_VERSION\")"
            }
            useKtorClient -> {
                """
                    |implementation("com.expediagroup", "graphql-kotlin-ktor-client", "$DEFAULT_PLUGIN_VERSION") {
                    |  exclude("com.expediagroup", "graphql-kotlin-client-serialization")
                    |}
                    |implementation("com.expediagroup", "graphql-kotlin-client-jackson", "$DEFAULT_PLUGIN_VERSION")
                """.trimMargin()
            }
            serializer == GraphQLSerializer.KOTLINX -> {
                """
                    |implementation("com.expediagroup", "graphql-kotlin-spring-client", "$DEFAULT_PLUGIN_VERSION") {
                    |  exclude("com.expediagroup", "graphql-kotlin-client-jackson")
                    |}
                    |implementation("com.expediagroup", "graphql-kotlin-client-serialization", "$DEFAULT_PLUGIN_VERSION")
                """.trimMargin()
            }
            else -> {
                "implementation(\"com.expediagroup:graphql-kotlin-spring-client:$DEFAULT_PLUGIN_VERSION\")"
            }
        }
        testProjectDirectory.generateBuildFileForClient(buildFileContents, clientDependencies, serializer)
        testProjectDirectory.createTestFile("JUnitQuery.graphql", "src/main/resources")
            .writeText(testQuery)
        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(
                loadTemplate(
                    "Application",
                    mapOf(
                        "ktorClient" to useKtorClient,
                        "useJackson" to (serializer == GraphQLSerializer.JACKSON),
                        "defaultHeader" to mapOf("name" to defaultHeaderName, "value" to defaultHeaderValue),
                        "requestHeader" to mapOf("name" to customHeaderName, "value" to customHeaderValue),
                        "optionalInputWrapper" to true
                    )
                )
            )

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments("build", "run", "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$DOWNLOAD_SDL_TASK_NAME")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_CLIENT_TASK_NAME")?.outcome)
        assertTrue(File(testProjectDirectory, "build/schema.graphql").exists())
        assertTrue(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/JUnitQuery.kt").exists())
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":run")?.outcome)
    }

    @Test
    @Tag("groovy")
    fun `apply the plugin extension to generate client (groovy)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        // custom header to pass to SDL endpoint
        val customHeaderName = "X-Custom-Header"
        val customHeaderValue = "My-Custom-Header-Value"
        WireMock.reset()
        WireMock.stubFor(stubSdlEndpoint().withHeader(customHeaderName, EqualToPattern(customHeaderValue)))
        WireMock.stubFor(stubGraphQLResponse())

        val buildFileContents =
            """
            |application {
            |  applicationDefaultJvmArgs = ["-DgraphQLEndpoint=${wireMockServer.baseUrl()}/graphql"]
            |  mainClassName = "com.example.ApplicationKt"
            |}
            |
            |graphql {
            |    client {
            |        sdlEndpoint = "${wireMockServer.baseUrl()}/sdl"
            |        packageName = "com.example.generated"
            |        // optional configuration
            |        allowDeprecatedFields = true
            |        customScalars = [new GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter")]
            |        headers = ["X-Custom-Header" : "My-Custom-Header-Value"]
            |        queryFiles = [
            |            file("$testProjectDirectory/src/main/resources/queries/JUnitQuery.graphql"),
            |            file("$testProjectDirectory/src/main/resources/queries/DeprecatedQuery.graphql")
            |        ]
            |        serializer = GraphQLSerializer.JACKSON
            |        timeout { t ->
            |            t.connect = 10000
            |            t.read = 30000
            |        }
            |    }
            |}
            """.trimMargin()
        testProjectDirectory.generateGroovyBuildFileForClient(buildFileContents)
        testProjectDirectory.createTestFile("JUnitQuery.graphql", "src/main/resources/queries")
            .writeText(testQuery)
        testProjectDirectory.createTestFile("DeprecatedQuery.graphql", "src/main/resources/queries")
            .writeText(loadResource("mocks/DeprecatedQuery.graphql"))
        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("Application", mapOf("customScalarsEnabled" to true)))
        testProjectDirectory.createTestFile("UUIDScalarConverter.kt", "src/main/kotlin/com/example")
            .writeText(loadResource("mocks/UUIDScalarConverter.txt"))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments("build", "run", "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$DOWNLOAD_SDL_TASK_NAME")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_CLIENT_TASK_NAME")?.outcome)
        assertTrue(File(testProjectDirectory, "build/schema.graphql").exists())
        assertTrue(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/JUnitQuery.kt").exists())
        assertTrue(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/DeprecatedQuery.kt").exists())
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":run")?.outcome)
    }

    @Test
    @Tag("kts")
    fun `apply the plugin extension to generate client using custom directory (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val buildFileContents =
            """
            |application {
            |  applicationDefaultJvmArgs = listOf("-DgraphQLEndpoint=${wireMockServer.baseUrl()}/graphql")
            |  mainClassName = "com.example.ApplicationKt"
            |}
            |
            |graphql {
            |  client {
            |    sdlEndpoint = "${wireMockServer.baseUrl()}/sdl"
            |    packageName = "com.example.generated"
            |
            |    // optional
            |    allowDeprecatedFields = true
            |    queryFileDirectory = "${'$'}{project.projectDir}/src/main/resources/queries"
            |  }
            |}
            """.trimMargin()
        testProjectDirectory.generateBuildFileForClient(buildFileContents)
        testProjectDirectory.createTestFile("JUnitQuery.graphql", "src/main/resources/queries")
            .writeText(testQuery)
        testProjectDirectory.createTestFile("DeprecatedQuery.graphql", "src/main/resources/queries")
            .writeText(loadResource("mocks/DeprecatedQuery.graphql"))
        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("Application", mapOf("customScalarsEnabled" to false)))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments("build", "run", "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$DOWNLOAD_SDL_TASK_NAME")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_CLIENT_TASK_NAME")?.outcome)
        assertTrue(File(testProjectDirectory, "build/schema.graphql").exists())
        assertTrue(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/JUnitQuery.kt").exists())
        assertTrue(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/DeprecatedQuery.kt").exists())
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":run")?.outcome)
    }
}
