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

import com.expediagroup.graphql.server.types.GraphQLRequest
import graphql.ExecutionInput
import org.dataloader.DataLoaderRegistry

/**
 * Convert the common [GraphQLRequest] to the execution input used by graphql-java
 */
fun GraphQLRequest.toExecutionInput(graphQLContext: Any? = null, dataLoaderRegistry: DataLoaderRegistry? = null, context: Map<*, Any>? = null): ExecutionInput {
    val builder = ExecutionInput.newExecutionInput()
        .query(this.query)
        .operationName(this.operationName)
        .variables(this.variables ?: emptyMap())
        .dataLoaderRegistry(dataLoaderRegistry ?: DataLoaderRegistry())

    graphQLContext?.let { builder.context(it) }
    context?.let { builder.graphQLContext(it) }

    return builder.build()
}
