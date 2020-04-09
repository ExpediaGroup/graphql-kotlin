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

import com.expediagroup.graphql.plugin.gradle.tasks.DOWNLOAD_SDL_TASK
import com.expediagroup.graphql.plugin.gradle.tasks.DownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_CLIENT_TASK
import com.expediagroup.graphql.plugin.gradle.tasks.GenerateClientTask
import com.expediagroup.graphql.plugin.gradle.tasks.INTROSPECT_SCHEMA_TASK
import com.expediagroup.graphql.plugin.gradle.tasks.IntrospectSchemaTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider

private const val PLUGIN_EXTENSION_NAME = "graphql"

/**
 * GraphQL Kotlin Gradle Plugin
 */
class GraphQLGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(PLUGIN_EXTENSION_NAME, GraphQLPluginExtension::class.java, project)

        val introspectSchemaTask = project.tasks.register(INTROSPECT_SCHEMA_TASK, IntrospectSchemaTask::class.java)
        configureIntrospectSchemaTask(project, introspectSchemaTask, extension)

        val downloadSDLTask = project.tasks.register(DOWNLOAD_SDL_TASK, DownloadSDLTask::class.java)
        configureDownloadSDLTask(project, downloadSDLTask, extension)

        val generateClientTask = project.tasks.register(GENERATE_CLIENT_TASK, GenerateClientTask::class.java)
        configureGenerateClientTask(project, generateClientTask, extension)
    }

    private fun configureIntrospectSchemaTask(project: Project, introspectSchemaTask: TaskProvider<IntrospectSchemaTask>, extension: GraphQLPluginExtension) {
        if (extension.endpoint != null) {
            introspectSchemaTask.configure { task ->
                task.endpoint.set(project.provider { extension.endpoint })
            }
        }
    }

    private fun configureDownloadSDLTask(project: Project, downloadSDLTask: TaskProvider<DownloadSDLTask>, extension: GraphQLPluginExtension) {
        if (extension.sdlEndpoint != null) {
            downloadSDLTask.configure {
                it.endpoint.set(project.provider { extension.sdlEndpoint })
            }
        }
    }

    private fun configureGenerateClientTask(
        project: Project,
        generateClientTask: TaskProvider<GenerateClientTask>,
        extension: GraphQLPluginExtension
    ) {
        if (extension.endpoint != null) {
            project.tasks.withType(IntrospectSchemaTask::class.java) { introspectTask ->
                generateClientTask.configure {
                    it.dependsOn(introspectTask.path)
                    it.schemaFile.set(introspectTask.outputFile)
                }
            }
        }

        if (extension.sdlEndpoint != null) {
            project.tasks.withType(DownloadSDLTask::class.java) { downloadSDLTask ->
                generateClientTask.configure {
                    it.dependsOn(downloadSDLTask.path)
                    it.schemaFile.set(downloadSDLTask.outputFile)
                }
            }
        }

        if (extension.packageName != null) {
            generateClientTask.configure {
                it.packageName.set(project.provider { extension.packageName })
            }
        }

        generateClientTask.configure { task ->
            task.allowDeprecatedFields.set(project.provider { extension.allowDeprecatedFields })
            task.scalarConverters.set(extension.scalarConverters)
            task.queryFiles.setFrom(extension.queryFiles.from)

            project.tasks.findByPath("compileKotlin")?.dependsOn(task.path)

            // configure generated directory source sets
            val outputDirectory = task.outputDirectory.get().asFile
            outputDirectory.mkdirs()

            val sourceSetContainer = project.findProperty("sourceSets") as? SourceSetContainer
            sourceSetContainer?.findByName("main")?.java?.srcDir(outputDirectory.path)
        }
    }
}
