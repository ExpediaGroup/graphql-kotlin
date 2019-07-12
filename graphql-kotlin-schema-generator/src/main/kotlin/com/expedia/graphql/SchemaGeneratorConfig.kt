package com.expedia.graphql

import com.expedia.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expedia.graphql.hooks.NoopSchemaGeneratorHooks
import com.expedia.graphql.hooks.SchemaGeneratorHooks

/**
 * Settings for generating the schema.
 */
data class SchemaGeneratorConfig(
    val supportedPackages: List<String>,
    val topLevelNames: TopLevelNames = TopLevelNames(),
    val hooks: SchemaGeneratorHooks = NoopSchemaGeneratorHooks(),
    val dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider = KotlinDataFetcherFactoryProvider(hooks)
)

/**
 * The names of the top level objects in the schema.
 */
data class TopLevelNames(
    val query: String = "Query",
    val mutation: String = "Mutation",
    val subscription: String = "Subscription"
)
