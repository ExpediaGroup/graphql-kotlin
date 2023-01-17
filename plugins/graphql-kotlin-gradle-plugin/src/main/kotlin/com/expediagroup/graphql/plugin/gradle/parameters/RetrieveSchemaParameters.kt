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

package com.expediagroup.graphql.plugin.gradle.parameters

import com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters
import java.io.File

/**
 * WorkParameters used for downloading SDL/introspecting GraphQL server.
 */
interface RetrieveSchemaParameters : WorkParameters {
    /** Target GraphQL server endpoint. */
    val endpoint: Property<String>
    /** Optional HTTP headers to be specified on a request. */
    val headers: MapProperty<String, Any>
    /** Optional timeout configuration for executing HTTP request. */
    val timeoutConfiguration: Property<TimeoutConfiguration>
    /** Target downloaded schema file. */
    val schemaFile: Property<File>
}
