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

package com.expediagroup.graphql.plugin.gradle

import com.expediagroup.graphql.plugin.gradle.config.GraphQLClientType
import com.expediagroup.graphql.plugin.gradle.tasks.DEFAULT_SCHEMA
import com.expediagroup.graphql.plugin.gradle.tasks.DOWNLOAD_SDL_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.FEDERATED_SCHEMA
import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_CLIENT_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_SDL_TASK_NAME
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
                headers = mapOf("$customHeaderName" to "$customHeaderValue")
                customScalars = listOf(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))
                queryFiles = listOf(
                    file("${'$'}{project.projectDir}/src/main/resources/queries/JUnitQuery.graphql"),
                    file("${'$'}{project.projectDir}/src/main/resources/queries/DeprecatedQuery.graphql")
                )
              }
            }
            """.trimIndent()
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
    fun `apply the plugin extension to generate client and execute customized ktor client (kts)`(@TempDir tempDir: Path) {
        verifyCustomizedClient(tempDir.toFile())
    }

    @Test
    @Tag("kts")
    fun `apply the plugin extension to generate client and execute customized spring web client (kts)`(@TempDir tempDir: Path) {
        verifyCustomizedClient(tempDir.toFile(), GraphQLClientType.WEBCLIENT)
    }

    private fun verifyCustomizedClient(testProjectDirectory: File, clientType: GraphQLClientType = GraphQLClientType.KTOR) {
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
            application {
              applicationDefaultJvmArgs = listOf("-DgraphQLEndpoint=${wireMockServer.baseUrl()}/graphql")
              mainClassName = "com.example.ApplicationKt"
            }

            graphql {
              client {
                sdlEndpoint = "${wireMockServer.baseUrl()}/sdl"
                packageName = "com.example.generated"
                clientType = GraphQLClientType.$clientType
              }
            }
            """.trimIndent()
        testProjectDirectory.generateBuildFileForClient(buildFileContents)
        testProjectDirectory.createTestFile("JUnitQuery.graphql", "src/main/resources")
            .writeText(testQuery)
        val useWebClient = clientType == GraphQLClientType.WEBCLIENT
        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(
                loadTemplate(
                    "Application",
                    mapOf(
                        "webClient" to useWebClient,
                        "defaultHeader" to mapOf("name" to defaultHeaderName, "value" to defaultHeaderValue),
                        "requestHeader" to mapOf("name" to customHeaderName, "value" to customHeaderValue)
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
            application {
              applicationDefaultJvmArgs = ["-DgraphQLEndpoint=${wireMockServer.baseUrl()}/graphql"]
              mainClassName = "com.example.ApplicationKt"
            }

            graphql {
                client {
                    sdlEndpoint = "${wireMockServer.baseUrl()}/sdl"
                    packageName = "com.example.generated"
                    // optional configuration
                    allowDeprecatedFields = true
                    clientType = GraphQLClientType.KTOR
                    customScalars = [new GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter")]
                    headers = ["X-Custom-Header" : "My-Custom-Header-Value"]
                    queryFiles = [
                        file("$testProjectDirectory/src/main/resources/queries/JUnitQuery.graphql"),
                        file("$testProjectDirectory/src/main/resources/queries/DeprecatedQuery.graphql")
                    ]
                    timeout { t ->
                        t.connect = 10000
                        t.read = 30000
                    }
                }
            }
            """.trimIndent()
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
                queryFileDirectory = "${'$'}{project.projectDir}/src/main/resources/queries"
              }
            }
            """.trimIndent()
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

    @Test
    @Tag("kts")
    fun `apply the plugin extension to generate SDL (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val buildFileContents =
            """
            graphql {
              schema {
                packages = listOf("com.example")
              }
            }
            """.trimIndent()
        testProjectDirectory.generateBuildFileForServer(buildFileContents)

        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("ServerApplication", mapOf("customScalarsEnabled" to false)))
        testProjectDirectory.createTestFile("HelloWorldQuery.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("HelloWorldQuery", mapOf("customScalarsEnabled" to false)))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(GENERATE_SDL_TASK_NAME, "--stacktrace")
            .build()
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_SDL_TASK_NAME")?.outcome)

        val generatedSchemaFile = File(testProjectDirectory, "build/schema.graphql")
        assertTrue(generatedSchemaFile.exists())
        assertEquals(DEFAULT_SCHEMA, generatedSchemaFile.readText().trim())
    }

    @Test
    @Tag("groovy")
    fun `apply the plugin extension to generate SDL (groovy)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val buildFileContents =
            """
            graphql {
              schema {
                packages = ["com.example"]
              }
            }
            """.trimIndent()
        testProjectDirectory.generateGroovyBuildFileForServer(buildFileContents)

        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("ServerApplication", mapOf("customScalarsEnabled" to false)))
        testProjectDirectory.createTestFile("HelloWorldQuery.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("HelloWorldQuery", mapOf("customScalarsEnabled" to false)))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(GENERATE_SDL_TASK_NAME, "--stacktrace")
            .build()
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_SDL_TASK_NAME")?.outcome)

        val generatedSchemaFile = File(testProjectDirectory, "build/schema.graphql")
        assertTrue(generatedSchemaFile.exists())
        assertEquals(DEFAULT_SCHEMA, generatedSchemaFile.readText().trim())
    }

    @Test
    @Tag("kts")
    fun `apply the plugin extension to generate SDL with custom hooks provider (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val buildFileContents =
            """
            graphql {
              schema {
                packages = listOf("com.example")
              }
            }
            """.trimIndent()
        val generateSDLDependencies = "graphqlSDL(\"com.expediagroup:graphql-kotlin-federated-hooks-provider:$DEFAULT_PLUGIN_VERSION\")"
        testProjectDirectory.generateBuildFileForServer(buildFileContents, generateSDLDependencies)

        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("ServerApplication", mapOf("customScalarsEnabled" to false)))
        testProjectDirectory.createTestFile("HelloWorldQuery.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("HelloWorldQuery", mapOf("customScalarsEnabled" to false)))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(GENERATE_SDL_TASK_NAME, "--stacktrace")
            .build()
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_SDL_TASK_NAME")?.outcome)

        val generatedSchemaFile = File(testProjectDirectory, "build/schema.graphql")
        assertTrue(generatedSchemaFile.exists())
        assertEquals(FEDERATED_SCHEMA, generatedSchemaFile.readText().trim())
    }

    @Test
    @Tag("groovy")
    fun `apply the plugin extension to generate SDL with custom hooks provider (groovy)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val buildFileContents =
            """
            graphql {
              schema {
                packages = ["com.example"]
              }
            }
            """.trimIndent()
        val generateSDLDependencies = "graphqlSDL \"com.expediagroup:graphql-kotlin-federated-hooks-provider:$DEFAULT_PLUGIN_VERSION\""
        testProjectDirectory.generateGroovyBuildFileForServer(buildFileContents, generateSDLDependencies)

        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("ServerApplication", mapOf("customScalarsEnabled" to false)))
        testProjectDirectory.createTestFile("HelloWorldQuery.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("HelloWorldQuery", mapOf("customScalarsEnabled" to false)))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(GENERATE_SDL_TASK_NAME, "--stacktrace")
            .build()
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_SDL_TASK_NAME")?.outcome)

        val generatedSchemaFile = File(testProjectDirectory, "build/schema.graphql")
        assertTrue(generatedSchemaFile.exists())
        assertEquals(FEDERATED_SCHEMA, generatedSchemaFile.readText().trim())
    }

    @Test
    @Tag("kts")
    fun `apply the plugin extension to generate SDL with custom hooks provider on classpath (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()

        val expectedFederatedSchemaWithCustomScalar =
            """
            schema {
              query: Query
            }

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Directs the executor to skip this field or fragment when the `if`'argument is true."
            directive @skip(
                "Skipped when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Marks the field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String = "No longer supported"
              ) on FIELD_DEFINITION | ENUM_VALUE

            "Exposes a URL that specifies the behaviour of this scalar."
            directive @specifiedBy(
                "The URL that specifies the behaviour of this scalar."
                url: String!
              ) on SCALAR

            "Marks target field as external meaning it will be resolved by federated schema"
            directive @external on FIELD_DEFINITION

            "Specifies required input field set from the base type for a resolver"
            directive @requires(fields: _FieldSet) on FIELD_DEFINITION

            "Specifies the base type field set that will be selectable by the gateway"
            directive @provides(fields: _FieldSet) on FIELD_DEFINITION

            "Space separated list of primary keys needed to access federated object"
            directive @key(fields: _FieldSet) on OBJECT | INTERFACE

            "Marks target object as extending part of the federated schema"
            directive @extends on OBJECT | INTERFACE

            type Query @extends {
              _service: _Service
              helloWorld(name: String): String!
              randomUUID: UUID!
            }

            type _Service {
              sdl: String!
            }

            "Custom scalar representing UUID"
            scalar UUID

            "Federation type representing set of fields"
            scalar _FieldSet
            """.trimIndent()
        val buildFileContents =
            """
            graphql {
              schema {
                packages = listOf("com.example")
              }
            }
            """.trimIndent()
        testProjectDirectory.generateBuildFileForServer(buildFileContents)

        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("ServerApplication", mapOf("customScalarsEnabled" to true)))
        testProjectDirectory.createTestFile("HelloWorldQuery.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("HelloWorldQuery", mapOf("customScalarsEnabled" to true)))
        testProjectDirectory.createTestFile("CustomHooksProvider.kt", "src/main/kotlin/com/example")
            .writeText(loadResource("mocks/CustomHooksProvider.txt"))
        testProjectDirectory.createTestFile("CustomFederatedHooks.kt", "src/main/kotlin/com/example")
            .writeText(loadResource("mocks/CustomFederatedHooks.txt"))
        testProjectDirectory.createTestFile("com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider", "src/main/resources/META-INF/services")
            .writeText("com.example.CustomHooksProvider")

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(GENERATE_SDL_TASK_NAME, "--stacktrace")
            .build()
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_SDL_TASK_NAME")?.outcome)

        val generatedSchemaFile = File(testProjectDirectory, "build/schema.graphql")
        assertTrue(generatedSchemaFile.exists())
        assertEquals(expectedFederatedSchemaWithCustomScalar, generatedSchemaFile.readText().trim())
    }
}
