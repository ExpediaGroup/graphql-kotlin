/*
 * Copyright 2019 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.federation.execution

/**
 * Simple registry that holds mapping of all registered federated GraphQL types and their corresponding resolvers.
 */
class FederatedTypeRegistry(federatedTypeResolvers: List<FederatedTypeResolver<*>> = emptyList()) {

    /**
     * Precompute the map of names to types so that we don't have to search the list every time.
     */
    private val namesToResolvers: Map<String, FederatedTypeResolver<*>> = federatedTypeResolvers.associateBy { it.typeName }

    /**
     * Retrieve target federated resolver for the specified GraphQL type.
     */
    fun getFederatedResolver(type: String) = namesToResolvers[type]
}
