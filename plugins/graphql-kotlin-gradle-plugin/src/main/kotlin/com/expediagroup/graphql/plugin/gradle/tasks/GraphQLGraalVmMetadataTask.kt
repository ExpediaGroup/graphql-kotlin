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

import com.expediagroup.graphql.plugin.gradle.actions.GenerateGraalVmMetadataAction
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.ClassLoaderWorkerSpec
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

internal const val GRAALVM_METADATA_TASK_NAME: String = "graphqLGraalVmMetadata"

abstract class GraphQLGraalVmMetadataTask : SourceTask() {

    @get:Classpath
    val pluginClasspath: ConfigurableFileCollection = project.objects.fileCollection()

    @get:Classpath
    val projectClasspath: ConfigurableFileCollection = project.objects.fileCollection()

    /**
     * List of supported packages that can contain GraphQL schema type definitions.
     */
    @Input
    val packages: ListProperty<String> = project.objects.listProperty(String::class.java)

    /**
     * Application main class name.
     */
    @Input
    @Optional
    val mainClassName: Property<String> = project.objects.property(String::class.java)

    @Inject
    abstract fun getWorkerExecutor(): WorkerExecutor

    @OutputDirectory
    val outputDirectory: DirectoryProperty = project.objects.directoryProperty()

    init {
        group = "GraphQL"
        description = "Generate GraalVM reflect metadata for GraphQL Kotlin servers."

        outputDirectory.convention(
            project.layout.buildDirectory.dir(
                "generated/graphqlGraalVmResources"
            )
        )
    }

    @TaskAction
    fun generateMetadata() {
        val packages = packages.get()
        if (packages.isEmpty()) {
            throw RuntimeException("attempt to generate SDL failed - missing required supportedPackages property")
        }

        val targetDirectory = outputDirectory.dir("META-INF/native-image/${project.group}/${project.name}/graphql").get().asFile
        if (!targetDirectory.isDirectory && !targetDirectory.mkdirs()) {
            throw RuntimeException("failed to generate target resource directory = $targetDirectory")
        }

        val workQueue: WorkQueue = getWorkerExecutor().classLoaderIsolation { workerSpec: ClassLoaderWorkerSpec ->
            val workerClasspath = pluginClasspath.plus(projectClasspath).plus(source.files)
            workerSpec.classpath.from(workerClasspath)
            logger.debug("worker classpath: \n${workerSpec.classpath.files.joinToString("\n")}")
        }

        logger.debug("submitting work item to generate GraalVM for the schema included in packages = {}", packages)
        workQueue.submit(GenerateGraalVmMetadataAction::class.java) { parameters ->
            parameters.supportedPackages.set(packages)
            parameters.mainClassName.set(mainClassName.orNull)
            parameters.outputDirectory.set(targetDirectory)
        }
        workQueue.await()
        logger.debug("successfully generated GraalVM metadata")
    }
}
