package com.expedia.graphql.federation

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelNames
import com.expedia.graphql.execution.KotlinDataFetcherFactoryProvider

/**
 * Settings for generating the federated schema.
 */
class FederatedSchemaGeneratorConfig(
    override val supportedPackages: List<String>,
    override val topLevelNames: TopLevelNames = TopLevelNames(),
    override val hooks: FederatedSchemaGeneratorHooks,
    override val dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider = KotlinDataFetcherFactoryProvider(hooks)
) : SchemaGeneratorConfig(supportedPackages, topLevelNames, hooks, dataFetcherFactoryProvider)
