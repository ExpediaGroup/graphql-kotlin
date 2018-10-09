package com.expedia.graphql.schema

import com.expedia.graphql.schema.hooks.NoopSchemaGeneratorHooks
import com.expedia.graphql.schema.hooks.SchemaGeneratorHooks
import graphql.schema.DataFetcherFactory

/**
 * Settings for generating the schema.
 */
data class SchemaGeneratorConfig(
    val supportedPackages: List<String>,
    val topLevelQueryName: String = "TopLevelQuery",
    val topLevelMutationName: String = "TopLevelMutation",
    val hooks: SchemaGeneratorHooks = NoopSchemaGeneratorHooks(),
    val dataFetcherFactory: DataFetcherFactory<*>? = null
)
