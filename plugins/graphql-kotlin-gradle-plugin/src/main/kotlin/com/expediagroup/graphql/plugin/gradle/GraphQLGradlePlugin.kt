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

import com.expediagroup.graphql.plugin.gradle.tasks.DOWNLOAD_SDL_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_CLIENT_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_SDL_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.GENERATE_TEST_CLIENT_TASK_NAME
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask
import com.expediagroup.graphql.plugin.gradle.tasks.INTROSPECT_SCHEMA_TASK_NAME
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.SourceSetContainer
import java.io.File

private const val PLUGIN_EXTENSION_NAME = "graphql"
private const val GENERATE_CLIENT_CONFIGURATION = "graphqlClient"
private const val GENERATE_SDL_CONFIGURATION = "graphqlSDL"

/**
 * GraphQL Kotlin Gradle Plugin
 */
@Suppress("UnstableApiUsage")
class GraphQLGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        configurePluginDependencies(project)
        registerTasks(project)

        val extension = project.extensions.create(PLUGIN_EXTENSION_NAME, GraphQLPluginExtension::class.java)
        project.afterEvaluate {
            processExtensionConfiguration(project, extension)
            configureTaskClasspaths(project)
        }
    }

    private fun configurePluginDependencies(project: Project) {
        project.configurations.create(GENERATE_CLIENT_CONFIGURATION) { configuration ->
            configuration.isVisible = true
            configuration.isTransitive = true
            configuration.description = "Configuration for generating GraphQL client"

            configuration.dependencies.add(project.dependencies.create("com.expediagroup:graphql-kotlin-client-generator:$DEFAULT_PLUGIN_VERSION"))
        }

        project.configurations.create(GENERATE_SDL_CONFIGURATION) { configuration ->
            configuration.isVisible = true
            configuration.isTransitive = true
            configuration.description = "Configuration for generating GraphQL schema in SDL format"

            configuration.dependencies.add(project.dependencies.create("com.expediagroup:graphql-kotlin-sdl-generator:$DEFAULT_PLUGIN_VERSION"))
        }
    }

    private fun registerTasks(project: Project) {
        project.tasks.register(DOWNLOAD_SDL_TASK_NAME, GraphQLDownloadSDLTask::class.java)
        project.tasks.register(GENERATE_CLIENT_TASK_NAME, GraphQLGenerateClientTask::class.java)
        project.tasks.register(GENERATE_TEST_CLIENT_TASK_NAME, GraphQLGenerateTestClientTask::class.java)
        project.tasks.register(GENERATE_SDL_TASK_NAME, GraphQLGenerateSDLTask::class.java)
        project.tasks.register(INTROSPECT_SCHEMA_TASK_NAME, GraphQLIntrospectSchemaTask::class.java)
    }

    private fun processExtensionConfiguration(project: Project, extension: GraphQLPluginExtension) {
        if (extension.isClientConfigurationAvailable()) {
            if (extension.clientExtension.packageName != null) {
                val generateClientTask = project.tasks.named(GENERATE_CLIENT_TASK_NAME, GraphQLGenerateClientTask::class.java).get()
                generateClientTask.packageName.convention(project.provider { extension.clientExtension.packageName })
                generateClientTask.allowDeprecatedFields.convention(project.provider { extension.clientExtension.allowDeprecatedFields })
                generateClientTask.customScalars.convention(extension.clientExtension.customScalars)
                val queryFileDirectory = extension.clientExtension.queryFileDirectory
                if (queryFileDirectory != null) {
                    generateClientTask.queryFileDirectory.set(File(queryFileDirectory))
                }
                generateClientTask.queryFiles.setFrom(extension.clientExtension.queryFiles)
                generateClientTask.serializer.convention(extension.clientExtension.serializer)
                generateClientTask.useOptionalInputWrapper.convention(extension.clientExtension.useOptionalInputWrapper)
                generateClientTask.parserOptions.convention(extension.clientExtension.parserOptions)

                when {
                    extension.clientExtension.endpoint != null -> {
                        val introspectSchemaTask = project.tasks.named(INTROSPECT_SCHEMA_TASK_NAME, GraphQLIntrospectSchemaTask::class.java).get()
                        introspectSchemaTask.endpoint.convention(project.provider { extension.clientExtension.endpoint })
                        introspectSchemaTask.headers.convention(project.provider { extension.clientExtension.headers })
                        introspectSchemaTask.timeoutConfig.convention(project.provider { extension.clientExtension.timeoutConfig })
                        generateClientTask.dependsOn(introspectSchemaTask.path)
                        generateClientTask.schemaFile.convention(introspectSchemaTask.outputFile)
                    }
                    extension.clientExtension.sdlEndpoint != null -> {
                        val downloadSDLTask = project.tasks.named(DOWNLOAD_SDL_TASK_NAME, GraphQLDownloadSDLTask::class.java).get()
                        downloadSDLTask.endpoint.convention(project.provider { extension.clientExtension.sdlEndpoint })
                        downloadSDLTask.headers.convention(project.provider { extension.clientExtension.headers })
                        downloadSDLTask.timeoutConfig.convention(project.provider { extension.clientExtension.timeoutConfig })
                        generateClientTask.dependsOn(downloadSDLTask.path)
                        generateClientTask.schemaFile.convention(downloadSDLTask.outputFile)
                    }
                    extension.clientExtension.schemaFile != null -> {
                        generateClientTask.schemaFile.set(extension.clientExtension.schemaFile)
                    }
                    else -> {
                        throw RuntimeException("Invalid GraphQL client extension configuration - missing required endpoint/sdlEndpoint/schemaFileName property")
                    }
                }
            }
        }

        if (extension.isSchemaConfigurationAvailable()) {
            val supportedPackages = extension.schemaExtension.packages
            if (supportedPackages.isEmpty()) {
                throw RuntimeException("Invalid GraphQL schema extension configuration - missing required supportedPackages property")
            }

            val generateSchemaTask = project.tasks.named(GENERATE_SDL_TASK_NAME, GraphQLGenerateSDLTask::class.java).get()
            generateSchemaTask.packages.set(supportedPackages)
        }
    }

    private fun configureTaskClasspaths(project: Project) {
        val isAndroidProject = project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin("com.android.library")
        val clientGeneratingTaskNames = mutableListOf<GraphQLGenerateClientTask>()
        val testClientGeneratingTaskNames = mutableListOf<GraphQLGenerateTestClientTask>()

        project.tasks.withType(GraphQLDownloadSDLTask::class.java).configureEach { downloadSDLTask ->
            val configuration = project.configurations.getAt(GENERATE_CLIENT_CONFIGURATION)
            downloadSDLTask.pluginClasspath.setFrom(configuration)
        }
        project.tasks.withType(GraphQLGenerateClientTask::class.java).configureEach { generateClientTask ->
            clientGeneratingTaskNames.add(generateClientTask)
            val configuration = project.configurations.getAt(GENERATE_CLIENT_CONFIGURATION)
            generateClientTask.pluginClasspath.setFrom(configuration)
            if (!isAndroidProject) {
                configureDefaultProjectSourceSet(project = project, outputDirectory = generateClientTask.outputDirectory)
            }
        }
        project.tasks.withType(GraphQLGenerateTestClientTask::class.java).configureEach { generateTestClientTask ->
            testClientGeneratingTaskNames.add(generateTestClientTask)
            val configuration = project.configurations.getAt(GENERATE_CLIENT_CONFIGURATION)
            generateTestClientTask.pluginClasspath.setFrom(configuration)
            if (!isAndroidProject) {
                configureDefaultProjectSourceSet(project = project, outputDirectory = generateTestClientTask.outputDirectory, targetSourceSet = "test")
            }
        }
        project.tasks.withType(GraphQLIntrospectSchemaTask::class.java).configureEach { introspectionTask ->
            val configuration = project.configurations.getAt(GENERATE_CLIENT_CONFIGURATION)
            introspectionTask.pluginClasspath.setFrom(configuration)
        }
        project.tasks.withType(GraphQLGenerateSDLTask::class.java).configureEach { generateSDLTask ->
            val sourceSetContainer = project.findProperty("sourceSets") as? SourceSetContainer
            val mainSourceSet = sourceSetContainer?.findByName("main")
            generateSDLTask.source(mainSourceSet?.output)
            generateSDLTask.projectClasspath.setFrom(mainSourceSet?.runtimeClasspath)

            val configuration = project.configurations.getAt(GENERATE_SDL_CONFIGURATION)
            generateSDLTask.pluginClasspath.setFrom(configuration)
            generateSDLTask.dependsOn(project.tasks.named("compileKotlin"))
        }

        if (isAndroidProject) {
            // Android plugins are eagerly configured so just adding the tasks outputs to the source set is not enough,
            // we also need to explicitly configure compile dependencies
            configureAndroidCompileTasks(project, clientGeneratingTaskNames, testClientGeneratingTaskNames)
        }
    }

    private fun configureDefaultProjectSourceSet(project: Project, outputDirectory: DirectoryProperty, targetSourceSet: String = "main") {
        val sourceSetContainer = project.findProperty("sourceSets") as? SourceSetContainer
        sourceSetContainer?.findByName(targetSourceSet)?.java?.srcDir(outputDirectory)
    }
}
