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
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Verifies failure scenarios only. Happy path scenarios are tested as part of integration/gradle-plugin-integration-tests composite build.
 */
class GraphQLGenerateClientTaskIT : WireMockAbstractIT() {

    @Test
    fun `generateClient task should fail on deprecated queries (kts)`(@TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        val sourceDirectory = File("src/integration/download-sdl-timeout")
        sourceDirectory.copyRecursively(testProjectDirectory)

        // version catalog setup
        File("../../gradle/libs.versions.toml").copyTo(File(testProjectDirectory, "gradle/libs.versions.toml"))

        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments(GENERATE_CLIENT_TASK_NAME, "--stacktrace")
            .buildAndFail()

        assertEquals(TaskOutcome.FAILED, buildResult.task(":$GENERATE_CLIENT_TASK_NAME")?.outcome)
        assertFalse(File(testProjectDirectory, "build/generated/source/graphql/main/com/example/generated/JUnitQuery.kt").exists())
    }
}
