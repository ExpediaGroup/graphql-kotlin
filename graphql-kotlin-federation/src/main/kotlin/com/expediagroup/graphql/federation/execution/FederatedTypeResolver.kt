package com.expediagroup.graphql.federation.execution

/**
 * Resolver used to retrieve target federated types.
 */
interface FederatedTypeResolver<out T> {

    /**
     * Resolves underlying federated types based on the passed in _entities query representations. Entities
     * need to be resolved in the same order they were specified by the list of representations. Each passed
     * in representation should either be resolved to a target entity OR NULL if entity cannot be resolved.
     *
     * @param representations _entity query representations that are required to instantiate the target type
     * @return list of the target federated type instances
     */
    suspend fun resolve(representations: List<Map<String, Any>>): List<T?>
}
