/*
 * Copyright 2022 Expedia, Inc
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

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals

class GraphQLGenerateClientIT {
    @ParameterizedTest
    @MethodSource("generateClientTests")
    fun `verify gradle plugin can generate client code`(sourceDirectory: File, @TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        sourceDirectory.copyRecursively(testProjectDirectory)

        val junitVersion = System.getProperty("junitVersion") ?: "5.8.2"
        val kotlinVersion = System.getProperty("kotlinVersion") ?: "1.7.10"
        val mockkVersion = System.getProperty("mockkVersion") ?: "1.12.5"
        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(
                "build",
                "--stacktrace"
            )
            .withEnvironment(
                mapOf(
                    "GRAPHQL_KOTLIN_VERSION" to DEFAULT_PLUGIN_VERSION,
                    "KOTLIN_VERSION" to kotlinVersion,
                    "JUNIT_VERSION" to junitVersion,
                    "MOCKK_VERSION" to mockkVersion
                )
            )
            .forwardOutput()
            .build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":build")?.outcome)
    }

    companion object {
        @JvmStatic
        fun generateClientTests(): List<Arguments> = locateTestCaseArguments("src/integration/client-generator")
    }
}
