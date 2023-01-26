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

package com.expediagroup.graphql.plugin.gradle.tasks

import com.expediagroup.graphql.plugin.gradle.WireMockAbstractIT
import com.github.tomakehurst.wiremock.client.WireMock
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Verifies failure scenarios only. Happy path scenarios are tested as part of integration/gradle-plugin-integration-tests composite build.
 */
class GraphQLIntrospectSchemaTaskIT : WireMockAbstractIT() {

    @Test
    fun `verify introspectSchema with timeout (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val sourceDirectory = File("src/integration/introspection-timeout")
        sourceDirectory.copyRecursively(testProjectDirectory)

        // version catalog setup
        File("../../gradle/libs.versions.toml").copyTo(File(testProjectDirectory, "gradle/libs.versions.toml"))

        WireMock.reset()
        WireMock.stubFor(stubIntrospectionResult(delay = 10_000))

        val result = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(INTROSPECT_SCHEMA_TASK_NAME, "--stacktrace")
            .withEnvironment(mapOf("wireMockServerUrl" to wireMockServer.baseUrl()))
            .buildAndFail()

        assertEquals(TaskOutcome.FAILED, result.task(":$INTROSPECT_SCHEMA_TASK_NAME")?.outcome)
        assertTrue(result.output.contains("Request timeout has expired", ignoreCase = true))
    }
}
