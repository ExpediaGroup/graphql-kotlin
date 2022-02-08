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

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.generator.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.execution.SimpleKotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.hooks.NoopSchemaGeneratorHooks
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLType

/**
 * Settings for generating the schema.
 */
open class SchemaGeneratorConfig(
    open val supportedPackages: List<String>,
    open val topLevelNames: TopLevelNames = TopLevelNames(),
    open val hooks: SchemaGeneratorHooks = NoopSchemaGeneratorHooks,
    open val dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider = SimpleKotlinDataFetcherFactoryProvider(),
    open val introspectionEnabled: Boolean = true,
    open val additionalTypes: Set<GraphQLType> = emptySet()
)
