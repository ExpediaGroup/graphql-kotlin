/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.plugin.maven

import com.expediagroup.graphql.plugin.schema.generateSDL
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.descriptor.PluginDescriptor
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import org.apache.maven.project.MavenProject
import java.io.File
import java.net.URLClassLoader

/**
 * Download GraphQL schema from a specified SDL endpoint.
 */
@Mojo(name = "generate-sdl", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE)
class GenerateSDLMojo : AbstractMojo() {

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private lateinit var project: MavenProject

    /**
     * Descriptor that holds current plugin information.
     */
    @Parameter(defaultValue = "\${plugin}", required = true, readonly = true)
    private lateinit var pluginDescriptor: PluginDescriptor

    /**
     * List of supported packages that can be scanned to generate schema.
     */
    @Parameter(name = "packages", required = true)
    private lateinit var packages: List<String>

    /**
     * Target GraphQL schema file.
     */
    @Parameter(defaultValue = "\${graphql.schemaFile}", name = "schemaFile")
    private var schemaFile: File? = null

    override fun execute() {
        val urls = project.compileClasspathElements
            .map { File(it.toString()).toURI() }
            .map { it.toURL() }
            .plus(pluginDescriptor.classRealm.urLs)
            .toTypedArray()
        val classLoader = URLClassLoader(urls, GenerateSDLMojo::class.java.classLoader)

        val outputDirectory = File(project.build.directory)
        if (!outputDirectory.isDirectory) {
            outputDirectory.mkdirs()
        }
        val graphQLSchemaFile = schemaFile ?: File("${outputDirectory.absolutePath}/schema.graphql")
        log.debug("attempting to generate SDL using custom classloader")
        executeWithClassloader(classLoader) {
            val schema = generateSDL(supportedPackages = packages)
            graphQLSchemaFile.writeText(schema)
        }
        log.debug("successfully generated SDL")
    }

    private fun executeWithClassloader(classLoader: ClassLoader, action: () -> Unit) {
        val originalClassLoader = Thread.currentThread().contextClassLoader
        try {
            Thread.currentThread().contextClassLoader = classLoader
            action.invoke()
        } finally {
            Thread.currentThread().contextClassLoader = originalClassLoader
        }
    }
}
