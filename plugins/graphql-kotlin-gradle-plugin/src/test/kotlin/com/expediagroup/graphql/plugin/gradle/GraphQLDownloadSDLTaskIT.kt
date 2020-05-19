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
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphQLDownloadSDLTaskIT : GraphQLGradlePluginAbstractIT() {

    @Test
    fun `apply the gradle plugin and execute downloadSDL task`(@TempDir tempDir: Path) {
        val buildFileContents = """
            val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
              endpoint.set("${wireMockServer.baseUrl()}/sdl")
            }
        """.trimIndent()
        generateDefaultBuildFile(tempDir.toFile()).appendText(buildFileContents)

        val result = GradleRunner.create()
            .withProjectDir(tempDir.toFile())
            .withPluginClasspath()
            .withArguments(DOWNLOAD_SDL_TASK_NAME)
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":$DOWNLOAD_SDL_TASK_NAME")?.outcome)
        assertTrue(File(tempDir.toFile(), "build/schema.graphql").exists())
    }
}
