/*
 * Copyright 2026 Expedia, Inc
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

import com.expediagroup.graphql.plugin.gradle.GraphQLGradlePlugin
import com.expediagroup.graphql.plugin.gradle.GraphQLPluginExtension
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphQLGenerateSDLTaskTest {

    @Test
    fun `jvmArguments defaults to an empty list`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java")
        project.pluginManager.apply(GraphQLGradlePlugin::class.java)

        val task = project.tasks.getByName(GENERATE_SDL_TASK_NAME) as GraphQLGenerateSDLTask
        assertTrue(task.jvmArguments.get().isEmpty())
    }

    @Test
    fun `jvmArguments configured on the schema extension are propagated to the task`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java")
        project.pluginManager.apply(GraphQLGradlePlugin::class.java)

        val extension = project.extensions.getByType(GraphQLPluginExtension::class.java)
        extension.schema {
            it.packages = listOf("com.example")
            it.jvmArguments = listOf("-Xmx2g", "-XX:+UseG1GC")
        }

        // extension configuration is applied to the task in an afterEvaluate hook
        (project as ProjectInternal).evaluate()

        val task = project.tasks.getByName(GENERATE_SDL_TASK_NAME) as GraphQLGenerateSDLTask
        assertEquals(listOf("-Xmx2g", "-XX:+UseG1GC"), task.jvmArguments.get())
    }
}
