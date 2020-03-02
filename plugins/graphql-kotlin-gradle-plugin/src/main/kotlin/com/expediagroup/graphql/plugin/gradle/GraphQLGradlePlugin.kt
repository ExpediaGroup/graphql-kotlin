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

private const val PLUGIN_EXTENSION_NAME = "graphql"

class GraphQLGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(PLUGIN_EXTENSION_NAME, GraphQLPluginExtension::class.java, project)

        project.tasks.register(INTROSPECT_SCHEMA_TASK, IntrospectSchemaTask::class.java) {
            it.endpoint.set(extension.endpoint)
            it.outputFileName.set(extension.schemaFileName)
        }
        project.tasks.register(DOWNLOAD_SDL_TASK, DownloadSDLTask::class.java) {
            it.endpoint.set(extension.sdlEndpoint)
            it.outputFileName.set(extension.schemaFileName)
        }
        val generateClientTask = project.tasks.register(GENERATE_CLIENT_TASK, GenerateClientTask::class.java) { task ->
            task.schemaFileName.set(extension.schemaFileName)
            task.schemaFile.set(extension.schemaFile)
            task.packageName.set(extension.packageName)
            task.queryFileDirectory.set(extension.queryFileDirectory)
        }

        project.tasks.findByPath("compileKotlin")?.dependsOn(GENERATE_CLIENT_TASK)

        // configure generated directory source sets
        val outputDirectory = generateClientTask.get().outputDirectory.get().asFile
        outputDirectory.mkdirs()

        val sourceSetContainer = project.property("sourceSets") as? SourceSetContainer
        sourceSetContainer?.findByName("main")?.java?.srcDir(outputDirectory.path)
    }
}
