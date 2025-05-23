/*
 * Copyright 2025 Expedia, Inc
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

import com.expediagroup.graphql.server.types.GraphQLBatchRequest
import com.expediagroup.graphql.server.types.GraphQLRequest
import graphql.ExecutionInput
import graphql.GraphQLContext
import org.dataloader.DataLoaderRegistry

internal val EMPTY_VARIABLES = emptyMap<String, Any?>()
internal val EMPTY_EXTENSIONS = emptyMap<String, Any?>()

/**
 * Convert the common [GraphQLRequest] to the [ExecutionInput] used by graphql-java
 */
fun GraphQLRequest.toExecutionInput(
    graphQLContext: GraphQLContext = GraphQLContext.getDefault(),
    dataLoaderRegistry: DataLoaderRegistry? = null
): ExecutionInput =
    ExecutionInput.newExecutionInput()
        .query(this.query)
        .operationName(this.operationName)
        .variables(this.variables ?: EMPTY_VARIABLES)
        .extensions(this.extensions ?: EMPTY_EXTENSIONS)
        .also { builder ->
            dataLoaderRegistry?.let {
                builder.dataLoaderRegistry(dataLoaderRegistry)
            }
        }
        .graphQLContext { graphQLContextBuilder ->
            graphQLContextBuilder.of(graphQLContext)
        }
        .build()

/**
 * Without doing a parsing attempt checks if the [GraphQLRequest] is a mutation
 */
fun GraphQLRequest.isMutation(): Boolean = query.contains("mutation ")

/**
 * Without doing a parsing attempt checks if the [GraphQLBatchRequest] contains a mutation
 */
fun GraphQLBatchRequest.containsMutation(): Boolean = requests.any(GraphQLRequest::isMutation)
