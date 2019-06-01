package com.expedia.graphql.hooks

import com.expedia.graphql.directives.KotlinDirectiveWiringFactory
import graphql.schema.GraphQLCodeRegistry

/**
 * Default hooks that do not override or set anything. Only used internally.
 * If you don't need hooks, the configuration will default to these.
 */
internal class NoopSchemaGeneratorHooks : SchemaGeneratorHooks {

    override val codeRegistry: GraphQLCodeRegistry.Builder
        get() = GraphQLCodeRegistry.newCodeRegistry()
    override val wiringFactory: KotlinDirectiveWiringFactory
        get() = KotlinDirectiveWiringFactory(codeRegistry)
}
