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

import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_CLIENT_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_TEST_CLIENT_TASK_NAME
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphQLGenerateClientTaskIT : GraphQLGradlePluginAbstractIT() {
    private val testQuery = loadResource("testQuery.graphql")

    @Test
    fun `apply the gradle plugin and execute generateClient task`(@TempDir tempDir: Path) {
        val testDirectory = tempDir.toFile()
        val buildFileContents = """
            application {
              applicationDefaultJvmArgs = listOf("-DgraphQLEndpoint=${wireMockServer.baseUrl()}/graphql")
              mainClassName = "com.example.ApplicationKt"
            }

            val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
              packageName.set("com.example.generated")
              schemaFileName.set("${'$'}{project.projectDir}/schema.graphql")
              converters.put("UUID", ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter"))
            }
        """.trimIndent()
        generateDefaultBuildFile(testDirectory).appendText(buildFileContents)

        // project setup
        File(testDirectory, "schema.graphql").writeText(testSchema)

        val resourcesDir = File(testDirectory, "src/main/resources")
        resourcesDir.mkdirs()
        File(resourcesDir, "JUnitQuery.graphql").writeText(testQuery)

        val srcDir = File(testDirectory, "src/main/kotlin/com/example")
        srcDir.mkdirs()
        File(srcDir, "Application.kt").writeText(loadResource("testApplication.txt"))
        // end project setup

        val codeGenerationResult = GradleRunner.create()
            .withProjectDir(testDirectory)
            .withPluginClasspath()
            .withArguments(GENERATE_CLIENT_TASK_NAME)
            .build()

        assertEquals(TaskOutcome.SUCCESS, codeGenerationResult.task(":$GENERATE_CLIENT_TASK_NAME")?.outcome)
        assertTrue(File(testDirectory, "build/generated/source/graphql/main/com/example/generated/JUnitQuery.kt").exists())

        val integrationTestResult = GradleRunner.create()
            .withProjectDir(testDirectory)
            .withPluginClasspath()
            .withArguments("run")
            .build()
        assertEquals(TaskOutcome.SUCCESS, integrationTestResult.task(":run")?.outcome)
    }

    @Test
    fun `apply the gradle plugin and execute generateTestClient task`(@TempDir tempDir: Path) {
        val testDirectory = tempDir.toFile()
        val buildFileContents = """
            val graphqlGenerateTestClient by tasks.getting(GraphQLGenerateClientTask::class) {
              packageName.set("com.example.generated")
              schemaFileName.set("${'$'}{project.projectDir}/schema.graphql")
            }

            tasks {
              test {
                systemProperty("graphQLEndpoint", "${wireMockServer.baseUrl()}/graphql")
              }
            }
        """.trimIndent()
        generateDefaultBuildFile(testDirectory).appendText(buildFileContents)

        // project setup
        File(testDirectory, "schema.graphql").writeText(testSchema)
        val testResourcesDir = File(testDirectory, "src/test/resources")
        testResourcesDir.mkdirs()
        File(testResourcesDir, "JUnitQuery.graphql").writeText(testQuery)

        val srcDir = File(testDirectory, "src/test/kotlin/com/example")
        srcDir.mkdirs()
        File(srcDir, "GenerateClientTest.kt").writeText(loadResource("testJunit.txt"))
        // end project setup

        val codeGenerationResult = GradleRunner.create()
            .withProjectDir(testDirectory)
            .withPluginClasspath()
            .withArguments(GENERATE_TEST_CLIENT_TASK_NAME)
            .build()

        assertEquals(TaskOutcome.SUCCESS, codeGenerationResult.task(":$GENERATE_TEST_CLIENT_TASK_NAME")?.outcome)
        assertTrue(File(testDirectory, "build/generated/source/graphql/test/com/example/generated/JUnitQuery.kt").exists())

        val integrationTestResult = GradleRunner.create()
            .withProjectDir(testDirectory)
            .withPluginClasspath()
            .withArguments("test")
            .build()
        assertEquals(TaskOutcome.SUCCESS, integrationTestResult.task(":test")?.outcome)
    }
}
