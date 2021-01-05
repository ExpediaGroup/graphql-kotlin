package com.expediagroup.graphql.spring.execution

import org.dataloader.DataLoaderRegistry

/**
 * Factory used to generate [DataLoaderRegistry] per GraphQL execution.
 */
interface DataLoaderRegistryFactory {

    /**
     * Generate [DataLoaderRegistry] to be used for GraphQL query execution.
     */
    fun generate(): DataLoaderRegistry
}

/**
 * Default [DataLoaderRegistryFactory] that generates empty data loader registry.
 */
class EmptyDataLoaderRegistryFactory : DataLoaderRegistryFactory {
    override fun generate() = DataLoaderRegistry()
}
