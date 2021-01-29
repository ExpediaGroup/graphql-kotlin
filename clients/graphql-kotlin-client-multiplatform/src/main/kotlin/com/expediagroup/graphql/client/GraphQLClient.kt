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

import com.expediagroup.graphql.types.GraphQLResponse

/**
 * A lightweight typesafe GraphQL HTTP client.
 */
interface GraphQLClient {

    /**
     * Executes specified GraphQL query or mutation.
     *
     * NOTE: explicit result type Class parameter is required due to the type erasure at runtime, i.e. since generic type is erased at runtime our
     * default serialization would attempt to serialize results back to Any object. As a workaround we get raw results as String which we then
     * manually deserialize using passed in result type Class information.
     */
    suspend fun <T> execute(query: String, operationName: String?, variables: Any?, resultType: Class<T>): GraphQLResponse<T>
}

suspend inline fun <reified T> GraphQLClient.execute(query: String, operationName: String? = null, variables: Any? = null): GraphQLResponse<T> =
    this.execute(query, operationName, variables, T::class.java)
