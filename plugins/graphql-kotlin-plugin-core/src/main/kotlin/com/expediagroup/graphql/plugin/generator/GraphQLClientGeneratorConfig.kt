package com.expediagroup.graphql.plugin.generator

class GraphQLClientGeneratorConfig(
    val packageName: String,
    val includeTypeName: Boolean = false,
    val allowDeprecated: Boolean = false
)
