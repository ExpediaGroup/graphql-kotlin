/*
 * Copyright 2019 Expedia Group, Inc.
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
