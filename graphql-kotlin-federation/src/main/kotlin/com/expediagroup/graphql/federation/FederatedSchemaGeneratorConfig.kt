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
