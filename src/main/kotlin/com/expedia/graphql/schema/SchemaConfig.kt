package com.expedia.graphql.schema

/**
 * Settings for generating the schema.
 */
data class SchemaConfig(
    val supportedPackages: String,
    val topLevelQueryName: String = "TopLevelQuery",
    val topLevelMutationName: String = "TopLevelMutation"
)
