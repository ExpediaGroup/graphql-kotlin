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

import com.expediagroup.graphql.plugin.gradle.actions.GenerateSDLAction
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.workers.ClassLoaderWorkerSpec
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

internal const val GENERATE_SDL_TASK_NAME: String = "graphqlGenerateSDL"

@Suppress("UnstableApiUsage")
abstract class GraphQLGenerateSDLTask : SourceTask() {

    @get:Classpath
    val pluginClasspath: ConfigurableFileCollection = project.objects.fileCollection()

    @get:Classpath
    val projectClasspath: ConfigurableFileCollection = project.objects.fileCollection()

    /**
     * List of supported packages that can contain GraphQL schema type definitions.
     */
    @Input
    @Optional
    @Option(option = "packages", description = "List of supported packages that can be scanned to generate SDL")
    val packages: ListProperty<String> = project.objects.listProperty(String::class.java)

    /**
     * Target GraphQL schema file to be generated.
     */
    @OutputFile
    val schemaFile: RegularFileProperty = project.objects.fileProperty()

    @Inject
    abstract fun getWorkerExecutor(): WorkerExecutor

    init {
        group = "GraphQL"
        description = "Generate GraphQL schema in SDL format."

        schemaFile.convention(project.layout.buildDirectory.file("schema.graphql"))
    }

    @TaskAction
    fun generateSDLAction() {
        val packages = packages.get()
        if (packages.isEmpty()) {
            throw RuntimeException("attempt to generate SDL failed - missing required supportedPackages property")
        }

        val generatedSchemaFile = schemaFile.asFile.get()
        val targetDirectory = generatedSchemaFile.parentFile
        if (!targetDirectory.isDirectory && !targetDirectory.mkdirs()) {
            throw RuntimeException("failed to generate target schema directory = $targetDirectory")
        }

        val workQueue: WorkQueue = getWorkerExecutor().classLoaderIsolation { workerSpec: ClassLoaderWorkerSpec ->
            val workerClasspath = pluginClasspath.plus(projectClasspath).plus(source.files)
            workerSpec.classpath.from(workerClasspath)
            logger.debug("worker classpath: \n${workerSpec.classpath.files.joinToString("\n")}")
        }

        logger.debug("submitting work item to generate SDL for the supported packages = $packages")
        workQueue.submit(GenerateSDLAction::class.java) { parameters ->
            parameters.supportedPackages.set(packages)
            parameters.schemaFile.set(generatedSchemaFile)
        }
        workQueue.await()
        logger.debug("successfully generated GraphQL schema")
    }
}
