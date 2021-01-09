package com.example.hooks

import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider

class CustomHooksProvider : SchemaGeneratorHooksProvider {

    override fun hooks(): SchemaGeneratorHooks = CustomFederatedHooks()
}
