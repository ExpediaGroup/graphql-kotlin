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

import com.expediagroup.graphql.plugin.downloadSchema
import kotlinx.coroutines.runBlocking
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import java.io.File

/**
 * Download GraphQL schema from a specified SDL endpoint.
 */
@Mojo(name = "downloadSDL", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
class DownloadSDLMojo : AbstractMojo() {

    /**
     * Target GraphQL server SDL endpoint.
     */
    @Parameter(defaultValue = "\${graphql.endpoint}", name = "endpoint", required = true)
    private lateinit var endpoint: String

    @Parameter(defaultValue = "\${project.build.directory}", readonly = true)
    private lateinit var outputDirectory: File

    @Suppress("EXPERIMENTAL_API_USAGE")
    override fun execute() {
        log.debug("executing downloadSDL MOJO against $endpoint")
        val schemaFile = File("${outputDirectory.absolutePath}/schema.graphql")
        runBlocking {
            val schema = downloadSchema(endpoint = endpoint)
            schemaFile.writeText(schema)
        }
        log.debug("successfully downloaded SDL")
    }
}
