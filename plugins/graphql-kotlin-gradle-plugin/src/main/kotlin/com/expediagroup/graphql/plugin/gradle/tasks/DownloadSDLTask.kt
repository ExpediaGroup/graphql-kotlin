package com.expediagroup.graphql.plugin.gradle.tasks

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

internal const val DOWNLOAD_SDL_TASK: String = "downloadSDL"

@Suppress("UnstableApiUsage")
open class DownloadSDLTask : DefaultTask() {

    @Input
    @Option(option = "endpoint", description = "target SDL endpoint")
    val endpoint: Property<String> = project.objects.property(String::class.java)

    @Input
    @Option(option = "outputFileName", description = "target schema file name")
    val outputFileName: Property<String> = project.objects.property(String::class.java)

    @OutputFile
    val outputFile: Provider<RegularFile> = outputFileName.flatMap { name -> project.layout.buildDirectory.file(name) }

    init {
        group = "GraphQL"
        description = "Download schema in SDL format from target endpoint."

        outputFileName.convention("schema.graphql")
    }

    @KtorExperimentalAPI
    @TaskAction
    fun downloadSDL() {
        logger.debug("starting introspection task against ${endpoint.get()}")
        runBlocking {
            HttpClient(CIO).use { client ->
                val sdl = client.get<String>(urlString = endpoint.get())
                outputFile.get().asFile.writeText(sdl)
            }
        }
    }
}
