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

package com.expediagroup.graphql.plugin.graalvm

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.InputStream

private const val DEFAULT_REFLECT_CONFIG = "default-reflect-config.json"
private const val DEFAULT_RESOURCE_CONFIG = "default-resource-config.json"

object DefaultMetadataLoader {
    /**
     * Load default GraalVM reflect metadata required to run native graphql-kotlin servers
     */
    fun loadDefaultReflectMetadata(): List<ClassMetadata> {
        val defaultResources = DefaultMetadataLoader.javaClass.classLoader.getResourceAsStream(DEFAULT_REFLECT_CONFIG)
            ?: throw IllegalStateException("Unable to load graphql-kotlin GraalVM reflect metadata")
        val mapper = jacksonObjectMapper()
        return mapper.readValue(defaultResources, object : TypeReference<List<ClassMetadata>>() {})
    }

    /**
     * Open up InputStream to default GraalVM resource config file to be used with graphql-kotlin servers.
     */
    fun defaultResourceMetadataStream(): InputStream =
        DefaultMetadataLoader.javaClass.classLoader.getResourceAsStream(DEFAULT_RESOURCE_CONFIG)
            ?: throw IllegalStateException("Unable to load graphql-kotlin GraalVM resources metadata")
}
