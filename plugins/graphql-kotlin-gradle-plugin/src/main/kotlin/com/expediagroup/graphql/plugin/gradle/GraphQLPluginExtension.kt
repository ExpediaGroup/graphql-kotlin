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

package com.expediagroup.graphql.plugin.gradle

import com.expediagroup.graphql.plugin.config.TimeoutConfig
import com.expediagroup.graphql.plugin.generator.GraphQLClientType
import com.expediagroup.graphql.plugin.generator.ScalarConverterMapping
import org.gradle.api.Action
import java.io.File

/**
 * GraphQL Kotlin Gradle Plugin extension.
 */
@Suppress("UnstableApiUsage")
open class GraphQLPluginExtension {

    private var clientExtensionConfigured: Boolean = false
    internal val clientExtension: GraphQLPluginClientExtension by lazy {
        clientExtensionConfigured = true
        GraphQLPluginClientExtension()
    }

    /** Plugin configuration for generating GraphQL client. */
    fun client(action: Action<GraphQLPluginClientExtension>) {
        action.execute(clientExtension)
    }

    internal fun isClientConfigurationAvailable(): Boolean = clientExtensionConfigured
}

open class GraphQLPluginClientExtension {
    /** GraphQL server endpoint that will be used to for running introspection queries. Alternatively you can download schema directly from [sdlEndpoint]. */
    var endpoint: String? = null
    /** GraphQL server SDL endpoint that will be used to download schema. Alternatively you can run introspection query against [endpoint]. */
    var sdlEndpoint: String? = null
    /** Target package name to be used for generated classes. */
    var packageName: String? = null
    /** Optional HTTP headers to be specified on an introspection query or SDL request. */
    var headers: Map<String, Any> = emptyMap()
    /** Boolean flag indicating whether or not selection of deprecated fields is allowed. */
    var allowDeprecatedFields: Boolean = false
    /** Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values. */
    var converters: Map<String, ScalarConverterMapping> = emptyMap()
    /** List of query files to be processed. */
    var queryFiles: List<File> = emptyList()
    /** Type of GraphQL client implementation to generate. */
    var clientType: GraphQLClientType = GraphQLClientType.DEFAULT

    /** Connect and read timeout configuration for executing introspection query/download schema */
    internal val timeoutConfig: TimeoutConfig = TimeoutConfig()

    fun timeout(action: Action<TimeoutConfig>) {
        action.execute(timeoutConfig)
    }
}
