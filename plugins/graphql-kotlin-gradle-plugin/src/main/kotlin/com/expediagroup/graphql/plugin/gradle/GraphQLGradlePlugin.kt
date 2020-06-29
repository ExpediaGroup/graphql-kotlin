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
@file:Suppress("UnstableApiUsage")
package com.expediagroup.graphql.plugin.gradle

import com.expediagroup.graphql.plugin.gradle.tasks.DOWNLOAD_SDL_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_CLIENT_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_TEST_CLIENT_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask
import com.expediagroup.graphql.plugin.gradle.tasks.INTROSPECT_SCHEMA_TASK_NAME
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import java.io.File

private const val PLUGIN_EXTENSION_NAME = "graphql"

/**
 * GraphQL Kotlin Gradle Plugin
 */
class GraphQLGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(PLUGIN_EXTENSION_NAME, GraphQLPluginExtension::class.java)

        project.tasks.register(INTROSPECT_SCHEMA_TASK_NAME, GraphQLIntrospectSchemaTask::class.java)
        project.tasks.register(DOWNLOAD_SDL_TASK_NAME, GraphQLDownloadSDLTask::class.java)
        project.tasks.register(GENERATE_CLIENT_TASK_NAME, GraphQLGenerateClientTask::class.java) { generateClientTask ->
            configureCompileTaskDependency(project = project, generateClientTaskPath = generateClientTask.path)

            generateClientTask.queryFileDirectory.convention("${project.projectDir}/src/main/resources")
            generateClientTask.outputDirectory.convention(project.layout.buildDirectory.dir("generated/source/graphql/main"))
        }
        project.tasks.register(GENERATE_TEST_CLIENT_TASK_NAME, GraphQLGenerateClientTask::class.java) { generateTestClientTask ->
            configureCompileTaskDependency(project = project, generateClientTaskPath = generateTestClientTask.path, compileTaskName = "compileTestKotlin")

            generateTestClientTask.description = "Generate HTTP test client from the specified GraphQL queries."
            generateTestClientTask.queryFileDirectory.convention("${project.projectDir}/src/test/resources")
            generateTestClientTask.outputDirectory.convention(project.layout.buildDirectory.dir("generated/source/graphql/test"))
        }

        project.afterEvaluate {
            if (extension.isClientConfigurationAvailable()) {
                if (extension.clientExtension.packageName != null) {
                    val generateClientTask = project.tasks.named(GENERATE_CLIENT_TASK_NAME, GraphQLGenerateClientTask::class.java).get()
                    generateClientTask.packageName.convention(project.provider { extension.clientExtension.packageName })
                    generateClientTask.allowDeprecatedFields.convention(project.provider { extension.clientExtension.allowDeprecatedFields })
                    generateClientTask.converters.convention(extension.clientExtension.converters)
                    generateClientTask.queryFiles.setFrom(extension.clientExtension.queryFiles)

                    if (extension.clientExtension.endpoint != null) {
                        val introspectSchemaTask = project.tasks.named(INTROSPECT_SCHEMA_TASK_NAME, GraphQLIntrospectSchemaTask::class.java).get()
                        introspectSchemaTask.endpoint.convention(project.provider { extension.clientExtension.endpoint })
                        introspectSchemaTask.headers.convention(project.provider { extension.clientExtension.headers })
                        introspectSchemaTask.timeoutConfig.convention(project.provider { extension.clientExtension.timeoutConfig })
                        generateClientTask.dependsOn(introspectSchemaTask.path)
                        generateClientTask.schemaFile.convention(introspectSchemaTask.outputFile)
                    } else if (extension.clientExtension.sdlEndpoint != null) {
                        val downloadSDLTask = project.tasks.named(DOWNLOAD_SDL_TASK_NAME, GraphQLDownloadSDLTask::class.java).get()
                        downloadSDLTask.endpoint.convention(project.provider { extension.clientExtension.sdlEndpoint })
                        downloadSDLTask.headers.convention(project.provider { extension.clientExtension.headers })
                        downloadSDLTask.timeoutConfig.convention(project.provider { extension.clientExtension.timeoutConfig })
                        generateClientTask.dependsOn(downloadSDLTask.path)
                        generateClientTask.schemaFile.convention(downloadSDLTask.outputFile)
                    } else {
                        throw RuntimeException("Invalid GraphQL client extension configuration - missing required endpoint/sdlEndpoint property")
                    }
                }
            }

            project.tasks.named(GENERATE_CLIENT_TASK_NAME, GraphQLGenerateClientTask::class.java) { task ->
                configureProjectSourceSet(project = project, outputDirectory = task.outputDirectory.get().asFile)
            }
            project.tasks.named(GENERATE_TEST_CLIENT_TASK_NAME, GraphQLGenerateClientTask::class.java) { task ->
                configureProjectSourceSet(project = project, outputDirectory = task.outputDirectory.get().asFile, targetSourceSet = "test")
            }
        }
    }

    private fun configureCompileTaskDependency(project: Project, generateClientTaskPath: String, compileTaskName: String = "compileKotlin") {
        val compileKotlinTask = project.tasks.findByPath(compileTaskName)
        if (compileKotlinTask == null) {
            throw RuntimeException("$compileKotlinTask task not found")
        } else {
            compileKotlinTask.dependsOn(generateClientTaskPath)
        }
    }

    private fun configureProjectSourceSet(project: Project, outputDirectory: File, targetSourceSet: String = "main") {
        outputDirectory.mkdirs()

        val sourceSetContainer = project.findProperty("sourceSets") as? SourceSetContainer
        sourceSetContainer?.findByName(targetSourceSet)?.java?.srcDir(outputDirectory.path)
    }
}
