package com.expediagroup.graphql.generator.federation.execution

import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

sealed interface FederatedTypeFetcher<T> {
    /**
     * This is the GraphQL name of the type [T]. It is used when running the resolvers and inspecting the
     * GraphQL "__typename" property during the entities requests
     */
    val typeName: String
}

interface FederatedTypeSuspendFetcher<T> : FederatedTypeFetcher<T> {
    /**
     * Resolves underlying federated types based on the passed in _entities query representations. Entities
     * need to be resolved in the same order they were specified by the list of representations. Each passed
     * in representation should either be resolved to a target entity OR NULL if entity cannot be resolved.
     *
     * @param environment DataFetchingEnvironment for executing this query
     * @param representations _entity query representations that are required to instantiate the target type
     * @return list of the target federated type instances
     */
    suspend fun resolve(environment: DataFetchingEnvironment, representations: List<Map<String, Any>>): List<T?>
}

interface FederatedTypeAsyncFetcher<T> : FederatedTypeFetcher<T> {
    /**
     * Resolves a non-blocking Promise of underlying federated types based on the passed in _entities query representations.
     * Entities need to be resolved in the same order they were specified by the list of representations. Each passed
     * in representation should either be resolved to a target entity OR NULL if entity cannot be resolved.
     *
     * @param environment DataFetchingEnvironment for executing this query
     * @param representations _entity query representations that are required to instantiate the target type
     * @return list of the target federated type instances
     */
    fun resolve(environment: DataFetchingEnvironment, representations: List<Map<String, Any>>): CompletableFuture<List<T?>>
}

