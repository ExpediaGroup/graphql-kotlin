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
import com.expediagroup.graphql.plugin.gradle.tasks.INTROSPECT_SCHEMA_TASK_NAME
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
    fun `apply the plugin extension to generate client`(@TempDir tempDir: Path) {
        val testDirectory = tempDir.toFile()
        val buildFileContents = """
            application {
              applicationDefaultJvmArgs = listOf("-DgraphQLEndpoint=${wireMockServer.baseUrl()}/graphql")
              mainClassName = "com.example.ApplicationKt"
            }

            graphql {
              client {
                endpoint = "${wireMockServer.baseUrl()}/graphql"
                packageName = "com.example.generated"
                converters.put("UUID", ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter"))
              }
            }
        """.trimIndent()
        generateDefaultBuildFile(testDirectory).appendText(buildFileContents)

        val resourcesDir = File(testDirectory, "src/main/resources")
        resourcesDir.mkdirs()
        File(resourcesDir, "JUnitQuery.graphql").writeText(loadResource("testQuery.graphql"))

        val srcDir = File(testDirectory, "src/main/kotlin/com/example")
        srcDir.mkdirs()
        File(srcDir, "Application.kt").writeText(loadResource("testApplication.txt"))

        val codeGenerationResult = GradleRunner.create()
            .withProjectDir(testDirectory)
            .withPluginClasspath()
            .withArguments("build")
            .build()

        assertEquals(TaskOutcome.SUCCESS, codeGenerationResult.task(":$INTROSPECT_SCHEMA_TASK_NAME")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, codeGenerationResult.task(":$GENERATE_CLIENT_TASK_NAME")?.outcome)
        assertTrue(File(testDirectory, "build/schema.graphql").exists())
        assertTrue(File(testDirectory, "build/generated/source/graphql/main/com/example/generated/JUnitQuery.kt").exists())

        val integrationTestResult = GradleRunner.create()
            .withProjectDir(testDirectory)
            .withPluginClasspath()
            .withArguments("run")
            .build()
        assertEquals(TaskOutcome.SUCCESS, integrationTestResult.task(":run")?.outcome)
    }
}
