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

package com.expediagroup.graphql.plugin.maven

import com.expediagroup.graphql.plugin.config.TimeoutConfig
import kotlinx.coroutines.runBlocking
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Parameter
import java.io.File

/**
 * Retrieve GraphQL schema by running introspection query or by downloading it directly from a specified SDL endpoint.
 */
abstract class RetrieveSchemaAbstractMojo : AbstractMojo() {

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

    @Parameter(defaultValue = "\${project.build.directory}", readonly = true)
    private lateinit var outputDirectory: File

    override fun execute() {
        log.debug("downloading GraphQL schema from $endpoint")
        if (!outputDirectory.isDirectory) {
            outputDirectory.mkdirs()
        }
        val schemaFile = File("${outputDirectory.absolutePath}/schema.graphql")
        runBlocking {
            val schema = retrieveGraphQLSchema(endpoint, headers, TimeoutConfig(connect = timeoutConfiguration.connect, read = timeoutConfiguration.read))
            schemaFile.writeText(schema)
        }
        log.debug("successfully downloaded schema")
    }

    abstract suspend fun retrieveGraphQLSchema(endpoint: String, httpHeaders: Map<String, Any>, timeoutConfig: TimeoutConfig): String
}

/**
 * Maven Plugin Property equivalent of [TimeoutConfig].
 *
 * Unfortunately we cannot use [TimeoutConfig] directly as per rules of mapping complex objects to Mojo parameters, target object has to be declared in
 * the same package as Mojo itself (otherwise we need to explicitly specify fully qualified implementation name in configuration XML block).
 *
 * @see [Guide to Configuring Plug-ins](https://maven.apache.org/guides/mini/guide-configuring-plugins.html#Mapping_Complex_Objects)
 */
class TimeoutConfiguration {
    /** Timeout in milliseconds to establish new connection. */
    @Parameter
    var connect: Long = 5_000

    /** Read timeout in milliseconds */
    @Parameter
    var read: Long = 15_000
}
