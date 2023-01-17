/*
 * Copyright 2023 Expedia, Inc
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

import com.expediagroup.graphql.plugin.gradle.actions.GenerateClientAction
import com.expediagroup.graphql.plugin.gradle.config.GraphQLParserOptions
import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.workers.ClassLoaderWorkerSpec
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject

/**
 * Generate GraphQL Kotlin client and corresponding data classes based on the provided GraphQL queries.
 */
abstract class AbstractGenerateClientTask : DefaultTask() {

    @get:Classpath
    val pluginClasspath: ConfigurableFileCollection = project.objects.fileCollection()

    /**
     * GraphQL schema file that will be used to generate client code.
     *
     * **Required Property**
     */
    @InputFile
    val schemaFile: RegularFileProperty = project.objects.fileProperty()

    /**
     * Target package name for generated code.
     *
     * **Required Property**
     * **Command line property is**: `packageName`.
     */
    @Input
    @Option(option = "packageName", description = "target package name to use for generated classes")
    val packageName: Property<String> = project.objects.property(String::class.java)

    /**
     * Boolean flag indicating whether selection of deprecated fields is allowed or not.
     *
     * **Default value is:** `false`.
     * **Command line property is**: `allowDeprecatedFields`.
     */
    @Input
    @Optional
    @Option(option = "allowDeprecatedFields", description = "boolean flag indicating whether selection of deprecated fields is allowed or not")
    val allowDeprecatedFields: Property<Boolean> = project.objects.property(Boolean::class.java)

    /**
     * List of custom GraphQL scalar converters.
     *
     * ```kotlin
     * customScalars.add(GraphQLScalar("UUID", "java.util.UUID", "com.expediagroup.graphql.examples.client.UUIDScalarConverter"))
     * )
     */
    @Input
    @Optional
    val customScalars: ListProperty<GraphQLScalar> = project.objects.listProperty(GraphQLScalar::class.java)

    /**
     * Directory containing GraphQL queries. Defaults to `src/main/resources` when generating main sources and `src/test/resources`
     * when generating test client.
     *
     * Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.
     */
    @InputDirectory
    @Optional
    val queryFileDirectory: DirectoryProperty = project.objects.directoryProperty()

    /**
     * List of query files to be processed. Instead of a list of files to be processed you can also specify [queryFileDirectory] directory
     * containing all the files. If this property is specified it will take precedence over the corresponding directory property.
     */
    @InputFiles
    @Optional
    val queryFiles: ConfigurableFileCollection = project.objects.fileCollection()

    @Input
    @Optional
    @Option(option = "serializer", description = "JSON serializer that will be used to generate the data classes.")
    val serializer: Property<GraphQLSerializer> = project.objects.property(GraphQLSerializer::class.java)

    @Input
    @Optional
    @Option(option = "useOptionalInputWrapper", description = "Opt-in flag to wrap nullable arguments in OptionalInput that supports both null and undefined.")
    val useOptionalInputWrapper: Property<Boolean> = project.objects.property(Boolean::class.java)

    @Input
    @Optional
    val parserOptions: Property<GraphQLParserOptions> = project.objects.property(GraphQLParserOptions::class.java)

    @OutputDirectory
    val outputDirectory: DirectoryProperty = project.objects.directoryProperty()

    @Inject
    abstract fun getWorkerExecutor(): WorkerExecutor

    init {
        group = "GraphQL"
        description = "Generate HTTP client from the specified GraphQL queries."

        allowDeprecatedFields.convention(false)
        customScalars.convention(emptyList())
        serializer.convention(GraphQLSerializer.JACKSON)
        useOptionalInputWrapper.convention(false)
        parserOptions.convention(GraphQLParserOptions())
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    @TaskAction
    fun generateGraphQLClientAction() {
        logger.debug("generating GraphQL client")

        val graphQLSchemaPath = when {
            schemaFile.isPresent -> schemaFile.get().asFile.path
            else -> throw RuntimeException("schema not available")
        }
        val targetPackage = packageName.orNull ?: throw RuntimeException("package not specified")
        val targetQueryFiles: List<File> = when {
            queryFiles.files.isNotEmpty() -> queryFiles.files.toList()
            queryFileDirectory.isPresent -> {
                queryFileDirectory.get().asFile
                    .listFiles { file -> file.extension == "graphql" }
                    ?.toList() ?: throw RuntimeException("exception while looking up the query files")
            }
            else -> throw RuntimeException("no query files found")
        }

        if (targetQueryFiles.isEmpty()) {
            throw RuntimeException("no query files specified")
        }

        val targetDirectory = outputDirectory.get().asFile
        if (!targetDirectory.isDirectory && !targetDirectory.mkdirs()) {
            throw RuntimeException("failed to generate generated source directory = $targetDirectory")
        }

        logConfiguration(graphQLSchemaPath, targetQueryFiles)
        val workQueue: WorkQueue = getWorkerExecutor().classLoaderIsolation { workerSpec: ClassLoaderWorkerSpec ->
            workerSpec.classpath.from(pluginClasspath)
            logger.debug("worker classpath: \n${workerSpec.classpath.files.joinToString("\n")}")
        }

        workQueue.submit(GenerateClientAction::class.java) { parameters ->
            parameters.packageName.set(targetPackage)
            parameters.allowDeprecated.set(allowDeprecatedFields)
            parameters.customScalars.set(customScalars)
            parameters.serializer.set(serializer)
            parameters.schemaPath.set(graphQLSchemaPath)
            parameters.queryFiles.set(targetQueryFiles)
            parameters.targetDirectory.set(targetDirectory)
            parameters.useOptionalInputWrapper.set(useOptionalInputWrapper)
            parameters.parserOptions.set(parserOptions)
        }
        workQueue.await()
        logger.debug("successfully generated GraphQL HTTP client")
    }

    private fun logConfiguration(schemaPath: String, queryFiles: List<File>) {
        logger.debug("GraphQL Client generator configuration:")
        logger.debug("  schema file = $schemaPath")
        logger.debug("  queries")
        queryFiles.forEach {
            logger.debug("    - ${it.name}")
        }
        logger.debug("  packageName = $packageName")
        logger.debug("  allowDeprecatedFields = $allowDeprecatedFields")
        logger.debug("  parserOptions = $parserOptions")
        logger.debug("  converters")
        customScalars.get().forEach { (customScalar, type, converter) ->
            logger.debug("    - custom scalar = $customScalar")
            logger.debug("      |- type = $type")
            logger.debug("      |- converter = $converter")
        }
        logger.debug("")
        logger.debug("-- end GraphQL Client generator configuration --")
    }
}
