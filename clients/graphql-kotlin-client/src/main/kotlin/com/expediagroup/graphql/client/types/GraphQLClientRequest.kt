/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.client.types

import kotlin.reflect.KClass

/**
 * Interface representing GraphQL request that follows the common GraphQL HTTP request format.
 *
 * @see [GraphQL Over HTTP](https://graphql.org/learn/serving-over-http/#post-request) for additional details
 */
interface GraphQLClientRequest<T : Any> {
    val query: String?
        get() = null
    val operationName: String?
        get() = null
    val variables: Any?
        get() = null
    val extensions: Map<String, Any>?
        get() = null

    /**
     * Parameterized type of a corresponding GraphQLResponse.
     */
    fun responseType(): KClass<T>
}
