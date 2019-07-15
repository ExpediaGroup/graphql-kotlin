package com.expedia.graphql

import com.expedia.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expedia.graphql.hooks.NoopSchemaGeneratorHooks
import com.expedia.graphql.hooks.SchemaGeneratorHooks

/**
 * Settings for generating the schema.
 */
open class SchemaGeneratorConfig(
    open val supportedPackages: List<String>,
    open val topLevelNames: TopLevelNames = TopLevelNames(),
    open val hooks: SchemaGeneratorHooks = NoopSchemaGeneratorHooks(),
    open val dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider = KotlinDataFetcherFactoryProvider(hooks)
)

/**
 * The names of the top level objects in the schema.
 */
data class TopLevelNames(
    val query: String = "Query",
    val mutation: String = "Mutation",
    val subscription: String = "Subscription"
)
