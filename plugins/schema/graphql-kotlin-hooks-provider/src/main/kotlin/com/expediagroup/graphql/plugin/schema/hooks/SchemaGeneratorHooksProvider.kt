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

package com.expediagroup.graphql.plugin.schema.hooks

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks

/**
 * Service provider interface that is used by the graphql-kotlin plugins to generate GraphQL schema in SDL format.
 *
 * Providers should be packaged in a JAR that contains provider configuration file under `src/main/resources/META-INF/services`. Provider file should be named
 * the same as implementing interface - `SchemaGeneratorHooksProvider`.
 * Provider configuration file should contain only single entry - fully qualified name of provider implementation.
 *
 * **See Also**: [ServiceLoader documentation](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html)
 */
interface SchemaGeneratorHooksProvider {

    /**
     * Create a new instance of a SchemaGeneratorHooks that will be used to generate GraphQL schema in SDL format.
     */
    fun hooks(): SchemaGeneratorHooks
}
