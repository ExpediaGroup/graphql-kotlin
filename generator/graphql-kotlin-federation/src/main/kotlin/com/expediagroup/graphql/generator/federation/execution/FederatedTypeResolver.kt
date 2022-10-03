/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.execution

/**
 * Abstraction that provides a convenient way to resolve underlying federated types based on the passed
 * in _entities query representations. Entities need to be resolved in the same order they were specified
 * by the list of representations. Each passed in representation should either be resolved to a target
 * entity OR NULL if entity cannot be resolved.
 */
sealed interface FederatedTypeResolver {
    /**
     * This is the GraphQL name of the type. It is used when running the resolvers and inspecting the
     * GraphQL "__typename" property during the entities requests.
     */
    val typeName: String
}
