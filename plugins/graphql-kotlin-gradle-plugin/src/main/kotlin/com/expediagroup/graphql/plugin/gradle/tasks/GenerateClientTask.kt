package com.expediagroup.graphql.plugin.gradle.tasks

import com.expediagroup.graphql.plugin.generateClient
import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorConfig
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

internal const val GENERATE_CLIENT_TASK: String = "generateClient"

@Suppress("UnstableApiUsage")
open class GenerateClientTask : DefaultTask() {

    @Input
    @Optional
    @Option(option = "schemaFileName", description = "target GraphQL schema file that will be used to generate the client code")
    val schemaFileName: Property<String> = project.objects.property(String::class.java)

    @InputFile
    @Optional
    val schemaFile: RegularFileProperty = project.objects.fileProperty()

    @Input
    @Option(option = "packageName", description = "target package name to use for generated classes")
    val packageName: Property<String> = project.objects.property(String::class.java)

    @Input
    @Optional
    @Option(option = "queryFileDirectory", description = "directory containing query files, defaults to src/main/resources")
    val queryFileDirectory: Property<String> = project.objects.property(String::class.java)

    @InputFiles
    @Optional
    val queryFiles: ConfigurableFileCollection = project.objects.fileCollection()

    @OutputDirectory
    val outputDirectory: Provider<Directory> = project.layout.buildDirectory.dir("generated/source/graphql")

    init {
        group = "GraphQL"
        description = "Generate HTTP client from the specified GraphQL queries.\nExamples:"

        queryFileDirectory.convention("${project.projectDir}/src/main/resources")
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    @TaskAction
    fun generateGraphQLClient() {
        val graphQLSchema = when {
            schemaFile.isPresent -> schemaFile.get().asFile
            schemaFileName.isPresent -> File(schemaFileName.get())
            else -> throw RuntimeException("schema not available")
        }

        val targetPackage = packageName.orNull ?: throw RuntimeException("package not specified")
        val targetQueryFiles: List<File> = when {
            queryFiles.files.isNotEmpty() -> queryFiles.files.toList()
            queryFileDirectory.isPresent -> File(queryFileDirectory.get())
                .listFiles { file -> file.extension == "graphql" }
                ?.toList() ?: throw RuntimeException("exception while looking up the query files")
            else -> throw RuntimeException("no query files found")
        }

        val targetDirectory = outputDirectory.get().asFile
        println("TARGET DIR ${targetDirectory.path}")
        if (!targetDirectory.isDirectory && !targetDirectory.mkdirs()) {
            throw RuntimeException("failed to generate generated source directory")
        }
        val config = GraphQLClientGeneratorConfig(packageName = targetPackage)
        generateClient(config, graphQLSchema, targetQueryFiles).forEach {
            it.writeTo(targetDirectory)
        }
    }
}
