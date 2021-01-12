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

package com.expediagroup.graphql.plugin.gradle.actions

import com.expediagroup.graphql.plugin.client.generateClient
import com.expediagroup.graphql.plugin.client.generator.GraphQLClientType
import com.expediagroup.graphql.plugin.client.generator.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.parameters.GenerateClientParameters
import org.gradle.workers.WorkAction

/**
 * WorkAction that generates GraphQL HTTP client and corresponding data classes based on the provided GraphQL queries.
 *
 * Action is run using Gradle classloader isolation with a custom classpath that has a dependency on `graphql-kotlin-client-generator`.
 */
@Suppress("UnstableApiUsage")
abstract class GenerateClientAction : WorkAction<GenerateClientParameters> {

    override fun execute() {
        val targetPackage = parameters.packageName.get()
        val allowDeprecated = parameters.allowDeprecated.get()
        val customScalarMap = parameters.customScalars.get().map { GraphQLScalar(it.scalar, it.type, it.converter) }
        val clientType = GraphQLClientType.valueOf(parameters.clientType.get().name)
        val schemaFile = parameters.schemaFile.get()
        val queryFiles = parameters.queryFiles.get()
        val targetDirectory = parameters.targetDirectory.get()

        generateClient(targetPackage, allowDeprecated, customScalarMap, clientType, schemaFile, queryFiles).forEach {
            it.writeTo(targetDirectory)
        }
    }
}
