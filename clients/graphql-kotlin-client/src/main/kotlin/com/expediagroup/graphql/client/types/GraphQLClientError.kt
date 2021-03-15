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

package com.expediagroup.graphql.client.types

/**
 * GraphQL error representation that is spec complaint with serialization and deserialization.
 *
 * @see [GraphQL Specification](http://spec.graphql.org/June2018/#sec-Errors) for additional details
 */
interface GraphQLClientError {
    /**
     * Description of the error.
     */
    val message: String

    /**
     * List of locations within the GraphQL document at which the exception occurred.
     */
    val locations: List<GraphQLClientSourceLocation>?
        get() = null

    /**
     * Path of the the response field that encountered the error.
     *
     * Path segments that represent fields should be strings, and path segments that represent list indices should be 0‐indexed integers. If the error happens in an aliased field, the path to the
     * error should use the aliased name, since it represents a path in the response, not in the query.
     */
    val path: List<Any>?
        get() = null

    /**
     * Additional information about the error.
     */
    val extensions: Map<String, Any?>?
        get() = null
}
