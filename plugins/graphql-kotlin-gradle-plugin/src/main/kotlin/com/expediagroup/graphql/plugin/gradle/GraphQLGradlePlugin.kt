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
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask
import com.expediagroup.graphql.plugin.gradle.tasks.INTROSPECT_SCHEMA_TASK_NAME
import org.gradle.api.Plugin
import org.gradle.api.Project

private const val PLUGIN_EXTENSION_NAME = "graphql"

/**
 * GraphQL Kotlin Gradle Plugin
 */
class GraphQLGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(PLUGIN_EXTENSION_NAME, GraphQLPluginExtension::class.java, project)
        project.tasks.register(INTROSPECT_SCHEMA_TASK_NAME, GraphQLIntrospectSchemaTask::class.java)
        project.tasks.register(DOWNLOAD_SDL_TASK_NAME, GraphQLDownloadSDLTask::class.java)

        project.afterEvaluate {
            if (extension.endpoint != null) {
                val introspectSchemaTask = project.tasks.named(INTROSPECT_SCHEMA_TASK_NAME, GraphQLIntrospectSchemaTask::class.java).get()
                introspectSchemaTask.endpoint.convention(project.provider { extension.endpoint })
            }

            if (extension.sdlEndpoint != null) {
                val downloadSDLTask = project.tasks.named(DOWNLOAD_SDL_TASK_NAME, GraphQLDownloadSDLTask::class.java).get()
                downloadSDLTask.endpoint.convention(project.provider { extension.endpoint })
            }
        }
    }
}
