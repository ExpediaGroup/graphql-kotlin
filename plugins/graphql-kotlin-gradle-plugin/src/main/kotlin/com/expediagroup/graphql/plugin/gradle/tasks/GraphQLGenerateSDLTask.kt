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
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.work.DisableCachingByDefault
import org.gradle.workers.ProcessWorkerSpec
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

internal const val GENERATE_SDL_TASK_NAME: String = "graphqlGenerateSDL"

@Suppress("UnstableApiUsage")
@DisableCachingByDefault(because = "Uses runtime classpath scanning and has not been audited for build cache reproducibility")
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

    /**
     * JVM arguments passed to the process-isolated worker that generates the SDL, e.g. `listOf("-Xmx2g")`.
     *
     * SDL generation runs in a forked JVM that uses the Gradle default heap. Schemas large enough to
     * exhaust that heap (resulting in an `OutOfMemoryError`) can raise it by configuring these arguments.
     * Defaults to an empty list, i.e. no additional JVM arguments.
     */
    @Input
    @Optional
    @Option(option = "jvm-args", description = "JVM arguments for the SDL generation worker, e.g. -Xmx2g")
    val jvmArguments: ListProperty<String> = project.objects.listProperty(String::class.java)

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    @get:Inject
    protected abstract val javaToolchainService: JavaToolchainService

    @get:Nested
    abstract val launcher: Property<JavaLauncher>

    init {
        group = "GraphQL"
        description = "Generate GraphQL schema in SDL format."

        schemaFile.convention(project.layout.buildDirectory.file("schema.graphql"))

        @Suppress("LeakingThis")
        project.extensions.getByType(JavaPluginExtension::class.java).toolchain
            .let(javaToolchainService::launcherFor)
            .let(launcher::convention)
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

        val workQueue: WorkQueue = workerExecutor.processIsolation { workerSpec: ProcessWorkerSpec ->
            workerSpec.forkOptions {
                it.setExecutable(launcher.get().executablePath.asFile)
                it.jvmArgs(jvmArguments.getOrElse(emptyList()))
            }
            logger.debug("worker executable: \n${workerSpec.forkOptions.executable}")
            logger.debug("worker jvm args: \n${workerSpec.forkOptions.jvmArgs}")

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
