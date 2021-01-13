/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.generator.execution

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Marker interface to indicate that the implementing class should be considered
 * as the GraphQL context. This means the implementing class will not appear in the schema.
 */
interface GraphQLContext

/**
 * Default [GraphQLContext] that can be used if there is none provided. Exposes generic concurrent hash map
 * that can be populated with custom data.
 */
class DefaultGraphQLContext : GraphQLContext {
    val contents: ConcurrentMap<Any, Any> = ConcurrentHashMap()
}
