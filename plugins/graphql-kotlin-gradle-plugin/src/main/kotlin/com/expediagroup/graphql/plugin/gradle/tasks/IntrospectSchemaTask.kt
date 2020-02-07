package com.expediagroup.graphql.plugin.gradle.tasks

import com.expediagroup.graphql.plugin.runIntrospectionQuery
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

internal const val INTROSPECT_SCHEMA_TASK: String = "introspectSchema"

@Suppress("UnstableApiUsage")
open class IntrospectSchemaTask : DefaultTask() {

    @Input
    @Option(option = "endpoint", description = "target GraphQL endpoint")
    val endpoint: Property<String> = project.objects.property(String::class.java)

    @Input
    @Option(option = "outputFileName", description = "target schema file name")
    val outputFileName: Property<String> = project.objects.property(String::class.java)

    @OutputFile
    val outputFile: Provider<RegularFile> = outputFileName.flatMap { name -> project.layout.buildDirectory.file(name) }

    init {
        group = "GraphQL"
        description = "Run introspection query against target GraphQL endpoint and save schema locally."

        outputFileName.convention("schema.graphql")
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    @TaskAction
    fun introspectSchema() {
        logger.debug("starting introspection task against ${endpoint.get()}")
        runBlocking {
            runIntrospectionQuery(endpoint = endpoint.get(), outputFile = outputFile.get().asFile)
        }
    }
}
