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

import com.expediagroup.graphql.plugin.gradle.actions.DownloadSDLAction
import com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.workers.ClassLoaderWorkerSpec
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

internal const val DOWNLOAD_SDL_TASK_NAME: String = "graphqlDownloadSDL"

/**
 * Task that attempts to download GraphQL schema in SDL format from the specified endpoint and save it locally.
 */
@Suppress("UnstableApiUsage")
abstract class GraphQLDownloadSDLTask : DefaultTask() {

    @get:Classpath
    val pluginClasspath: ConfigurableFileCollection = project.objects.fileCollection()

    /**
     * Target GraphQL server SDL endpoint that will be used to download schema.
     */
    @Input
    @Option(option = "endpoint", description = "target SDL endpoint")
    val endpoint: Property<String> = project.objects.property(String::class.java)

    /**
     * Optional HTTP headers to be specified on a SDL request.
     */
    @Input
    val headers: MapProperty<String, Any> = project.objects.mapProperty(String::class.java, Any::class.java)

    /**
     * Timeout configuration that specifies maximum amount of time (in milliseconds) to connect and download schema from SDL endpoint before we cancel the request.
     * Defaults to Ktor CIO engine defaults (5 seconds for connect timeout and 15 seconds for read timeout).
     */
    @Input
    val timeoutConfig: Property<TimeoutConfiguration> = project.objects.property(TimeoutConfiguration::class.java)

    /**
     * Target GraphQL schema file to be generated.
     */
    @OutputFile
    val outputFile: RegularFileProperty = project.objects.fileProperty()

    @Inject
    abstract fun getWorkerExecutor(): WorkerExecutor

    init {
        group = "GraphQL"
        description = "Download schema in SDL format from target endpoint."

        headers.convention(emptyMap())
        timeoutConfig.convention(TimeoutConfiguration())
        outputFile.convention(project.layout.buildDirectory.file("schema.graphql"))
    }

    /**
     * Download schema in SDL format from the specified endpoint and save it locally in the target output file.
     */
    @TaskAction
    fun downloadSDLAction() {
        val schemaFile = outputFile.asFile.get()
        val targetDirectory = schemaFile.parentFile
        if (!targetDirectory.isDirectory && !targetDirectory.mkdirs()) {
            throw RuntimeException("failed to generate target schema directory = $targetDirectory")
        }

        val workQueue: WorkQueue = getWorkerExecutor().classLoaderIsolation { workerSpec: ClassLoaderWorkerSpec ->
            workerSpec.classpath.from(pluginClasspath)
            logger.debug("worker classpath: \n${workerSpec.classpath.files.joinToString("\n")}")
        }

        logger.debug("submitting work item to download SDL from ${endpoint.get()} endpoint")
        workQueue.submit(DownloadSDLAction::class.java) { parameters ->
            parameters.endpoint.set(endpoint)
            parameters.headers.set(headers)
            parameters.timeoutConfiguration.set(timeoutConfig)
            parameters.schemaFile.set(schemaFile)
        }
        workQueue.await()
        logger.debug("successfully downloaded GraphQL schema")
    }
}
