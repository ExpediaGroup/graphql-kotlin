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

import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_CLIENT_TASK_NAME
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals

class GraphQLGradlePluginAndroidIT {

    @ParameterizedTest
    @MethodSource("androidTests")
    @EnabledIfEnvironmentVariable(named = "ANDROID_SDK_ROOT", matches = ".+")
    fun `verify gradle plugin can be applied on android projects`(sourceDirectory: File, @TempDir tempDir: Path) {
        val testProjectDirectory = tempDir.toFile()
        sourceDirectory.copyRecursively(testProjectDirectory)

        val androidPluginVersion = System.getProperty("androidPluginVersion") ?: "7.0.1"
        val kotlinVersion = System.getProperty("kotlinVersion") ?: "1.5.31"
        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withPluginClasspath()
            .withArguments("build", "--stacktrace", "-PGRAPHQL_KOTLIN_VERSION=$DEFAULT_PLUGIN_VERSION", "-PKOTLIN_VERSION=$kotlinVersion", "-PANDROID_PLUGIN_VERSION=$androidPluginVersion")
            .forwardOutput()
            .build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":app:$GENERATE_CLIENT_TASK_NAME")?.outcome)
    }

    companion object {
        @JvmStatic
        fun androidTests(): List<Arguments> = locateTestCaseArguments("src/integration/android")
    }
}
