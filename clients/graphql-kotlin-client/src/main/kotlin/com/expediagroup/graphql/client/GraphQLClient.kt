/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.client

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse

/**
 * A lightweight typesafe GraphQL HTTP client.
 */
interface GraphQLClient<RequestCustomizer> {

    /**
     * Executes [GraphQLClientRequest] and returns corresponding [GraphQLClientResponse].
     */
    suspend fun <T : Any> execute(request: GraphQLClientRequest<T>, requestCustomizer: RequestCustomizer.() -> Unit = {}): GraphQLClientResponse<T>

    /**
     * Executes batch requests that contains a number of [GraphQLClientRequest]s and returns a list of corresponding [GraphQLClientResponse]s.
     */
    suspend fun execute(requests: List<GraphQLClientRequest<*>>, requestCustomizer: RequestCustomizer.() -> Unit = {}): List<GraphQLClientResponse<*>>
}
