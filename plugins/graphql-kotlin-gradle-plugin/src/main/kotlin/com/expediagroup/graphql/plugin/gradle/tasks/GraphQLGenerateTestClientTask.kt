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

package com.expediagroup.graphql.plugin.gradle.tasks

internal const val GENERATE_TEST_CLIENT_TASK_NAME: String = "graphqlGenerateTestClient"

/**
 * Generate GraphQL Kotlin test client and corresponding data classes based on the provided GraphQL queries.
 */
@Suppress("UnstableApiUsage")
abstract class GraphQLGenerateTestClientTask : AbstractGenerateClientTask() {

    init {
        description = "Generate HTTP test client from the specified GraphQL queries."

        queryFileDirectory.convention(project.layout.projectDirectory.dir("src/test/resources"))
        outputDirectory.convention(project.layout.buildDirectory.dir("generated/source/graphql/test"))
    }
}
