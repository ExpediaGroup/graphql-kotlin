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

package com.expediagroup.graphql.plugin.gradle.tasks

import com.expediagroup.graphql.plugin.generateClient
import com.expediagroup.graphql.plugin.generator.ScalarConverterMapping
import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorConfig
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
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

internal const val GENERATE_CLIENT_TASK_NAME: String = "graphqlGenerateClient"

/**
 * Generate GraphQL Kotlin client and corresponding data classes based on the provided GraphQL queries.
 */
@Suppress("UnstableApiUsage")
open class GraphQLGenerateClientTask : DefaultTask() {

    @Input
    @Optional
    @Option(option = "schemaFileName", description = "path to GraphQL schema file that will be used to generate the client code")
    val schemaFileName: Property<String> = project.objects.property(String::class.java)

    @InputFile
    @Optional
    val schemaFile: RegularFileProperty = project.objects.fileProperty()

    @Input
    @Option(option = "packageName", description = "target package name to use for generated classes")
    val packageName: Property<String> = project.objects.property(String::class.java)

    @Input
    @Optional
    @Option(option = "allowDeprecatedFields", description = "boolean flag indicating whether selection of deprecated fields is allowed or not")
    val allowDeprecatedFields: Property<Boolean> = project.objects.property(Boolean::class.java)

    @Input
    @Optional
    val scalarConverters: MapProperty<String, ScalarConverterMapping> = project.objects.mapProperty(String::class.java, ScalarConverterMapping::class.java)

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
        description = "Generate HTTP client from the specified GraphQL queries."

        queryFileDirectory.convention("${project.projectDir}/src/main/resources")
        allowDeprecatedFields.convention(false)
        scalarConverters.convention(emptyMap())
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    @TaskAction
    fun generateGraphQLClientAction() {
        val graphQLSchema = when {
            schemaFile.isPresent -> schemaFile.get().asFile
            schemaFileName.isPresent -> File(schemaFileName.get())
            else -> throw RuntimeException("schema not available")
        }
        if (!graphQLSchema.isFile) {
            throw RuntimeException("specified schema file does not exist")
        }

        val targetPackage = packageName.orNull ?: throw RuntimeException("package not specified")
        val targetQueryFiles: List<File> = when {
            queryFiles.files.isNotEmpty() -> queryFiles.files.toList()
            queryFileDirectory.isPresent -> File(queryFileDirectory.get())
                .listFiles { file -> file.extension == "graphql" }
                ?.toList() ?: throw RuntimeException("exception while looking up the query files")
            else -> throw RuntimeException("no query files found")
        }

        if (targetQueryFiles.isEmpty()) {
            throw RuntimeException("no query files specified")
        }

        val targetDirectory = outputDirectory.get().asFile
        if (!targetDirectory.isDirectory && !targetDirectory.mkdirs()) {
            throw RuntimeException("failed to generate generated source directory")
        }
        val config = GraphQLClientGeneratorConfig(
            packageName = targetPackage,
            allowDeprecated = allowDeprecatedFields.get(),
            scalarTypeToConverterMapping = scalarConverters.get())
        generateClient(config, graphQLSchema, targetQueryFiles).forEach {
            it.writeTo(targetDirectory)
        }
    }
}
