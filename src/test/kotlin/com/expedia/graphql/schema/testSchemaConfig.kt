package com.expedia.graphql.schema

import com.expedia.graphql.schema.hooks.SchemaGeneratorHooks

val testSchemaConfig = SchemaGeneratorConfig(supportedPackages = "com.expedia")

fun getTestSchemaConfigWithHooks(hooks: SchemaGeneratorHooks) = SchemaGeneratorConfig(supportedPackages = "com.expedia", hooks = hooks)
