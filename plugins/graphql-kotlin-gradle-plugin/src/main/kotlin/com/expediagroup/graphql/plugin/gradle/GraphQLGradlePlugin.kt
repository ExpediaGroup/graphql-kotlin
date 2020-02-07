package com.expediagroup.graphql.plugin.gradle

import com.expediagroup.graphql.plugin.gradle.tasks.DOWNLOAD_SDL_TASK
import com.expediagroup.graphql.plugin.gradle.tasks.DownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.INTROSPECT_SCHEMA_TASK
import com.expediagroup.graphql.plugin.gradle.tasks.IntrospectSchemaTask
import org.gradle.api.Plugin
import org.gradle.api.Project

private const val PLUGIN_EXTENSION_NAME = "graphql"

class GraphQLGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(PLUGIN_EXTENSION_NAME, GraphQLPluginExtension::class.java, project)

        project.tasks.register(INTROSPECT_SCHEMA_TASK, IntrospectSchemaTask::class.java) {
            it.endpoint.set(extension.endpoint)
            it.outputFileName.set(extension.schemaFile)
        }
        project.tasks.register(DOWNLOAD_SDL_TASK, DownloadSDLTask::class.java) {
            it.endpoint.set(extension.sdlEndpoint)
            it.outputFileName.set(extension.schemaFile)
        }
    }
}
