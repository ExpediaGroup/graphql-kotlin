/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.spring.model

import com.expediagroup.graphql.spring.exception.SimpleKotlinGraphQLError
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import graphql.ExecutionResult
import graphql.GraphQLError
import java.lang.Exception

@JsonInclude(Include.NON_NULL)
data class GraphQLResponse(
    val data: Any? = null,
    val errors: List<GraphQLError>? = null,
    val extensions: Map<Any, Any>? = null
)

fun ExecutionResult.toGraphQLResponse(): GraphQLResponse {
    val filteredErrors = if (errors?.isNotEmpty() == true) {
        errors.map {
            when (it) {
                is SimpleKotlinGraphQLError -> it
                is Exception -> SimpleKotlinGraphQLError(exception = it, locations = it.locations, path = it.path, errorType = it.errorType)
                else -> it
            }
        }
    } else null
    val filteredExtensions = if (extensions?.isNotEmpty() == true) extensions else null
    return GraphQLResponse(getData(), filteredErrors, filteredExtensions)
}
