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

package com.expediagroup.graphql.server.execution

const val GRAPHQL_COROUTINE_CONTEXT_KEY = "GRAPHQL_COROUTINE_CONTEXT"

/**
 * Factory that generates GraphQL context.
 */
interface GraphQLContextFactory<in Request> {

    /**
     * GraphQL Java 17 has a new context map instead of a generic object. Implementing this method
     * will set the context map in the execution input.
     */
    suspend fun generateContextMap(request: Request): Map<*, Any?>? = null
}
