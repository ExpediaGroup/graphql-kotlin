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

package com.expediagroup.graphql.server.extensions

import com.expediagroup.graphql.server.types.GraphQLServerError
import com.expediagroup.graphql.server.types.GraphQLResponse
import com.expediagroup.graphql.server.types.GraphQLSourceLocation
import graphql.ExecutionResult
import graphql.GraphQLError as GraphQLJavaError
import graphql.language.SourceLocation

/**
 * Convert a graphql-java result to the common serializable type [GraphQLResponse]
 */
fun ExecutionResult.toGraphQLResponse(): GraphQLResponse<*> {
    val data: Any? = getData<Any?>()
    val filteredErrors: List<GraphQLServerError>? = if (errors?.isNotEmpty() == true) errors?.map { it.toGraphQLKotlinType() } else null
    val filteredExtensions: Map<Any, Any>? = if (extensions?.isNotEmpty() == true) extensions else null
    return GraphQLResponse(data, filteredErrors, filteredExtensions)
}

/**
 * Convert the graphql-java type to the common serializable type [GraphQLServerError]
 */
fun graphql.GraphQLError.toGraphQLKotlinType(): GraphQLServerError {
    val newExtensions = this.extensions?.toMutableMap() ?: mutableMapOf()
    newExtensions.putIfAbsent("classification", this.errorType.toSpecification(this))

    return GraphQLServerError(
        this.message.orEmpty(),
        this.locations?.map { it.toGraphQLKotlinType() },
        this.path,
        newExtensions
    )
}

/**
 * Convert the graphql-java type to the common serializable type [GraphQLSourceLocation]
 */
internal fun SourceLocation.toGraphQLKotlinType() = GraphQLSourceLocation(
    line,
    column
)
