package com.expedia.graphql.federation.execution

/**
 * Simple registry that holds mapping of all registered federated GraphQL types and their corresponding resolvers.
 */
class FederatedTypeRegistry(private val federatedTypeResolvers: Map<String, FederatedTypeResolver<*>>) {

    /**
     * Retrieve target federated resolver for the specified GraphQL type.
     */
    fun getFederatedResolver(type: String) = federatedTypeResolvers[type]
}
