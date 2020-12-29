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

import com.expediagroup.graphql.plugin.gradle.parameters.GenerateSDLParameters
import org.gradle.workers.WorkAction

/**
 * WorkAction that is used to generate GraphQL schema in SDL format.
 *
 * Action is run using Gradle classloader isolation with a custom classpath that has a dependency on `graphql-kotlin-sdl-generator`
 * and custom `SchemaGeneratorHooks` providers. Since we don't have direct dependency on the sdl-generator within the plugin
 * project, we utilize class loader and reflections to load and invoke `generateSDL` function directly from the classpath.
 */
@Suppress("UnstableApiUsage")
abstract class GenerateSDLAction : WorkAction<GenerateSDLParameters> {

    /**
     * Generate GraphQL schema in SDL format.
     */
    override fun execute() {
        val supportedPackages = parameters.getSupportedPackages().get()
        val schemaFile = parameters.getSchemaFileName().get()

        val generatorClass = this.javaClass.classLoader.loadClass("com.expediagroup.graphql.plugin.schema.GenerateSDLKt")
        val targetMethod = generatorClass.methods.find { it.name == "generateSDL" }
        if (targetMethod != null) {
            val schema = targetMethod.invoke(null, supportedPackages)
            schemaFile.writeText(schema.toString())
        } else {
            throw NoSuchMethodError("Unable to locate generateSDL target method")
        }
    }
}
