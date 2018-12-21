package com.expedia.graphql

import com.expedia.graphql.hooks.SchemaGeneratorHooks

val defaultSupportedPackages = listOf("com.expedia")
val testSchemaConfig = SchemaGeneratorConfig(defaultSupportedPackages)

fun getTestSchemaConfigWithHooks(hooks: SchemaGeneratorHooks) = SchemaGeneratorConfig(defaultSupportedPackages, hooks = hooks)
