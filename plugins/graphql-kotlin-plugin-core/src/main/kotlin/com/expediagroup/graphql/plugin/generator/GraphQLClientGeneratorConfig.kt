package com.expediagroup.graphql.plugin.generator

data class GraphQLClientGeneratorConfig(
    val packageName: String,
    val allowDeprecated: Boolean = false,
    val scalarTypeToConverterMapping: Map<String, CustomScalarConverterMapping> = emptyMap()
)

data class CustomScalarConverterMapping(
    /** Fully qualified class name of a custom scalar type, e.g. java.util.UUID */
    val type: String,
    /** Fully qualified class name of a custom converter used to convert to/from raw JSON and [type] */
    val converter: String
)
