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

import kotlinx.coroutines.runBlocking
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File

/**
 * Retrieve GraphQL schema by running introspection query or by downloading it directly from a specified SDL endpoint.
 */
abstract class RetrieveSchemaAbstractMojo : AbstractMojo() {

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private lateinit var project: MavenProject

    /**
     * Target endpoint.
     */
    @Parameter(defaultValue = "\${graphql.endpoint}", name = "endpoint", required = true)
    private lateinit var endpoint: String

    /**
     * Optional HTTP headers to be specified on a request.
     */
    @Parameter(name = "headers")
    private var headers: Map<String, Any> = mutableMapOf()

    /**
     * Timeout configuration that specifies maximum amount of time (in milliseconds) to connect and download schema before we cancel the request.
     * Defaults to Ktor CIO engine defaults (5 seconds for connect timeout and 15 seconds for read timeout).
     */
    @Parameter(name = "timeoutConfiguration")
    private var timeoutConfiguration: TimeoutConfiguration = TimeoutConfiguration()

    /**
     * Target GraphQL schema file.
     */
    @Parameter(defaultValue = "\${graphql.schemaFile}", name = "schemaFile")
    private var schemaFile: File? = null

    override fun execute() {
        log.debug("downloading GraphQL schema from $endpoint")
        val outputDirectory = File(project.build.directory)
        if (!outputDirectory.isDirectory) {
            outputDirectory.mkdirs()
        }

        val graphQLSchemaFile = schemaFile ?: File("${outputDirectory.absolutePath}/schema.graphql")
        runBlocking {
            val schema = retrieveGraphQLSchema(endpoint, headers, timeoutConfiguration)
            graphQLSchemaFile.writeText(schema)
        }
        log.debug("successfully downloaded schema")
    }

    abstract suspend fun retrieveGraphQLSchema(endpoint: String, httpHeaders: Map<String, Any>, timeoutConfiguration: TimeoutConfiguration): String
}

/**
 * Timeout configuration for executing introspection query and downloading schema SDL.
*/
class TimeoutConfiguration {
    /** Timeout in milliseconds to establish new connection. */
    @Parameter
    var connect: Long = 5_000

    /** Read timeout in milliseconds */
    @Parameter
    var read: Long = 15_000
}
