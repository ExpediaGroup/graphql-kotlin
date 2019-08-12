package com.expedia.graphql.federation

/**
 * Resolver used to retrieve target federated types.
 */
interface FederatedTypeResolver<T> {

    /**
     * Resolves underlying federated type based on the passed in _entity query representations.
     *
     * @param keys _entity query representations that are required to instantiate the type
     * @return target federated type instance
     */
    fun resolve(keys: Map<String, Any>): T
}
