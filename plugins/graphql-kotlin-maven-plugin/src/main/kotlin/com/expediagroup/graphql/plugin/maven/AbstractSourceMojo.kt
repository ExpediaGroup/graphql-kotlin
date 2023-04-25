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

package com.expediagroup.graphql.plugin.maven

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.descriptor.PluginDescriptor
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File
import java.net.URLClassLoader

/**
 * MOJO that generates some files based on the project sources.
 */
abstract class AbstractSourceMojo : AbstractMojo() {

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    internal lateinit var project: MavenProject

    /**
     * Descriptor that holds current plugin information.
     */
    @Parameter(defaultValue = "\${plugin}", required = true, readonly = true)
    private lateinit var pluginDescriptor: PluginDescriptor

    /**
     * Target directory where to store generated files.
     */
    abstract var outputDirectory: File

    override fun execute() {
        val urls = project.compileClasspathElements
            .map { File(it.toString()).toURI() }
            .map { it.toURL() }
            .plus(pluginDescriptor.classRealm.urLs)
            .toTypedArray()
        val classLoader = URLClassLoader(urls, AbstractSourceMojo::class.java.classLoader)

        if (!outputDirectory.isDirectory && !outputDirectory.mkdirs()) {
            throw RuntimeException("failed to create generated source directory")
        }
        executeWithClassloader(classLoader) {
            generate()
        }
    }

    /**
     * Generate files based on provided project sources.
     */
    abstract fun generate()

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
