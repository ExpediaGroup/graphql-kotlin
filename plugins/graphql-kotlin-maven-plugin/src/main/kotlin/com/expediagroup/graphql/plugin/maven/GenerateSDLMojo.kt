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

import com.expediagroup.graphql.plugin.schema.generateSDL
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import java.io.File

/**
 * Download GraphQL schema from a specified SDL endpoint.
 */
@Mojo(name = "generate-sdl", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE)
class GenerateSDLMojo : AbstractSourceMojo() {

    /**
     * List of supported packages that can be scanned to generate schema.
     */
    @Parameter(name = "packages", required = true)
    private lateinit var packages: List<String>

    /**
     * Target GraphQL schema file. Defaults to `schema.graphql`.
     */
    @Parameter(defaultValue = "\${graphql.schemaFile}", name = "schemaFile")
    private var schemaFile: File? = null

    /**
     * Target directory where to store generated SDL file, defaults to `target`.
     */
    @Parameter(defaultValue = "\${project.build.directory}", name = "outputDirectory")
    override lateinit var outputDirectory: File

    /**
     * Generate GraphQL schema file in SDL format.
     */
    override fun generate() {
        val graphQLSchemaFile = schemaFile ?: File("${outputDirectory.absolutePath}/schema.graphql")
        log.debug("attempting to generate SDL using custom classloader")
        val schema = generateSDL(supportedPackages = packages)
        graphQLSchemaFile.writeText(schema)
        log.debug("successfully generated SDL")
    }
}
