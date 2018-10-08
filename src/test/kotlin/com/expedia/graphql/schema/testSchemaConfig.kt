package com.expedia.graphql.schema

import com.expedia.graphql.schema.hooks.SchemaGeneratorHooks

val defaultSupportedPackages = listOf("com.expedia")
val testSchemaConfig = SchemaGeneratorConfig(defaultSupportedPackages)

fun getTestSchemaConfigWithHooks(hooks: SchemaGeneratorHooks) = SchemaGeneratorConfig(defaultSupportedPackages, hooks = hooks)
