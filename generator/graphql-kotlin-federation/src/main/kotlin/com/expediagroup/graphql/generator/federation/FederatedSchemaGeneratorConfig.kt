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

package com.expediagroup.graphql.generator.federation

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelNames
import com.expediagroup.graphql.generator.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.execution.SimpleKotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.internal.state.ClassScanner
import graphql.schema.GraphQLType

/**
 * Settings for generating the federated schema.
 */
@Suppress("LongParameterList")
class FederatedSchemaGeneratorConfig(
    override val supportedPackages: List<String>,
    override val topLevelNames: TopLevelNames = TopLevelNames(),
    override val hooks: FederatedSchemaGeneratorHooks,
    override val dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider = SimpleKotlinDataFetcherFactoryProvider(),
    override val introspectionEnabled: Boolean = true,
    override val additionalTypes: Set<GraphQLType> = emptySet(),
    override val typeResolver: FederatedGraphQLTypeResolver = FederatedClasspathTypeResolver(ClassScanner(supportedPackages))
) : SchemaGeneratorConfig(supportedPackages, topLevelNames, hooks, dataFetcherFactoryProvider, introspectionEnabled, additionalTypes, typeResolver)
