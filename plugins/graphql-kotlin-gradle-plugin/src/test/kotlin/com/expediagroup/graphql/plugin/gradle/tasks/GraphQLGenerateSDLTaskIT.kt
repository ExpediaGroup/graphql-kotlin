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

import com.expediagroup.graphql.plugin.gradle.DEFAULT_PLUGIN_VERSION
import com.expediagroup.graphql.plugin.gradle.GraphQLGradlePluginAbstractIT
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal val DEFAULT_SCHEMA =
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

    type Query {
      helloWorld(name: String): String!
    }
    """.trimIndent()

internal val FEDERATED_SCHEMA =
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
    }

    type _Service {
      sdl: String!
    }

    "Federation type representing set of fields"
    scalar _FieldSet
    """.trimIndent()

class GraphQLGenerateSDLTaskIT : GraphQLGradlePluginAbstractIT() {

    @Test
    @Tag("kts")
    fun `verify generateSDL task (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()

        val buildFileContents =
            """
            val graphqlGenerateSDL by tasks.getting(com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask::class) {
                packages.set(listOf("com.example"))
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
            .withArguments(GENERATE_SDL_TASK_NAME)
            .build()
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_SDL_TASK_NAME")?.outcome)

        val generatedSchemaFile = File(testProjectDirectory, "build/schema.graphql")
        assertTrue(generatedSchemaFile.exists())
        assertEquals(DEFAULT_SCHEMA, generatedSchemaFile.readText().trim())
    }

    @Test
    @Tag("groovy")
    fun `verify generateSDL task with custom hooks provider (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()

        val buildFileContents =
            """
            val graphqlGenerateSDL by tasks.getting(com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask::class) {
                packages.set(listOf("com.example"))
                hooksProvider.set("com.expediagroup:graphql-kotlin-federated-hooks-provider:$DEFAULT_PLUGIN_VERSION")
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
            .withArguments(GENERATE_SDL_TASK_NAME)
            .build()
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_SDL_TASK_NAME")?.outcome)

        val generatedSchemaFile = File(testProjectDirectory, "build/schema.graphql")
        assertTrue(generatedSchemaFile.exists())
        assertEquals(FEDERATED_SCHEMA, generatedSchemaFile.readText().trim())
    }

    @Test
    @Tag("groovy")
    fun `verify generateSDL task (groovy)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()

        val buildFileContents =
            """
            graphqlGenerateSDL {
                packages = ["com.example"]
            }
            """.trimIndent()
        testProjectDirectory.generateGroovyBuildFileForServer(buildFileContents)

        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("ServerApplication", emptyMap()))
        testProjectDirectory.createTestFile("HelloWorldQuery.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("HelloWorldQuery", emptyMap()))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(GENERATE_SDL_TASK_NAME)
            .build()
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_SDL_TASK_NAME")?.outcome)

        val generatedSchemaFile = File(testProjectDirectory, "build/schema.graphql")
        assertTrue(generatedSchemaFile.exists())
        assertEquals(DEFAULT_SCHEMA, generatedSchemaFile.readText().trim())
    }

    @Test
    @Tag("groovy")
    fun `verify generateSDL task with custom hooks provider (groovy)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()

        val buildFileContents =
            """
            graphqlGenerateSDL {
                packages = ["com.example"]
                hooksProvider = "com.expediagroup:graphql-kotlin-federated-hooks-provider:$DEFAULT_PLUGIN_VERSION"
            }
            """.trimIndent()
        testProjectDirectory.generateGroovyBuildFileForServer(buildFileContents)

        testProjectDirectory.createTestFile("Application.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("ServerApplication", emptyMap()))
        testProjectDirectory.createTestFile("HelloWorldQuery.kt", "src/main/kotlin/com/example")
            .writeText(loadTemplate("HelloWorldQuery", emptyMap()))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(GENERATE_SDL_TASK_NAME)
            .build()
        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$GENERATE_SDL_TASK_NAME")?.outcome)

        val generatedSchemaFile = File(testProjectDirectory, "build/schema.graphql")
        assertTrue(generatedSchemaFile.exists())
        assertEquals(FEDERATED_SCHEMA, generatedSchemaFile.readText().trim())
    }
}
