package com.expedia.graphql.schema

import com.expedia.graphql.schema.generator.completableFutureResolver
import com.expedia.graphql.schema.hooks.NoopSchemaGeneratorHooks
import com.expedia.graphql.schema.hooks.SchemaGeneratorHooks
import kotlin.reflect.KType

/**
 * Settings for generating the schema.
 */
data class SchemaGeneratorConfig(
    val supportedPackages: String,
    val topLevelQueryName: String = "TopLevelQuery",
    val topLevelMutationName: String = "TopLevelMutation",
    val hooks: SchemaGeneratorHooks = NoopSchemaGeneratorHooks(),
    val monadResolver: (KType) -> KType = completableFutureResolver
)
