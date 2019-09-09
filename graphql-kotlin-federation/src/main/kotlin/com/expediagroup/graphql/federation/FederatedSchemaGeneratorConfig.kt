/*
 * Copyright 2019 Expedia Group, Inc.
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

package com.expediagroup.graphql.federation

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelNames
import com.expediagroup.graphql.execution.KotlinDataFetcherFactoryProvider

/**
 * Settings for generating the federated schema.
 */
class FederatedSchemaGeneratorConfig(
    override val supportedPackages: List<String>,
    override val topLevelNames: TopLevelNames = TopLevelNames(),
    override val hooks: FederatedSchemaGeneratorHooks,
    override val dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider = KotlinDataFetcherFactoryProvider(hooks)
) : SchemaGeneratorConfig(supportedPackages, topLevelNames, hooks, dataFetcherFactoryProvider)
