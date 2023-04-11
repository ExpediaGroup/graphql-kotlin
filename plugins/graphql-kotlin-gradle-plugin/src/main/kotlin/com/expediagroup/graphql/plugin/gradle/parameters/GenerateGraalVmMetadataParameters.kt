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

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters
import java.io.File

/**
 * WorkParameters used for generating GraalVM reachability metadata for GraphQL schema.
 */
interface GenerateGraalVmMetadataParameters : WorkParameters {
    /** List of supported packages that can contain GraphQL schema type definitions. */
    val supportedPackages: ListProperty<String>
    /** Main application class name. */
    val mainClassName: Property<String>
    /** Directory where to store generated reachability metadata. */
    val outputDirectory: Property<File>
}
