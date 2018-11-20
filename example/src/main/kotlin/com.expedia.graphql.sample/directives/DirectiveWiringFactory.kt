package com.expedia.graphql.sample.directives

import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.schema.idl.WiringFactory

class DirectiveWiringFactory : WiringFactory {
    private val wiring = listOf(
            StringEvalDirectiveWiring(),
            CakeOnlyDirectiveWiring()
    )

    override fun providesSchemaDirectiveWiring(environment: SchemaDirectiveWiringEnvironment<*>): Boolean {
        return true
    }

    override fun getSchemaDirectiveWiring(environment: SchemaDirectiveWiringEnvironment<*>): SchemaDirectiveWiring? {
        return wiring.asSequence()
                .filter { it.isResponsible(environment) }
                .singleOrNull()
    }
}