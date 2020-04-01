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

package com.expediagroup.graphql.client

/**
 * GraphQL operation result representation.
 *
 * @see [GraphQL Specification](http://spec.graphql.org/June2018/#sec-Response-Format) for additional details
 */
data class GraphQLResult<T>(
    /**
     * Field that represents all fields selected in the given operation or NULL if error was encountered before execution (or during execution if it prevents valid response).
     */
    val data: T? = null,
    /**
     * Optional field that contains a list of [GraphQLError] that were encountered during query execution
     */
    val errors: List<GraphQLError>? = null,
    /**
     * Optional field that contains arbitrary map of additional data that was populated during query execution (e.g. tracing or metrics information).
     */
    val extensions: Map<String, Any>? = null
)
