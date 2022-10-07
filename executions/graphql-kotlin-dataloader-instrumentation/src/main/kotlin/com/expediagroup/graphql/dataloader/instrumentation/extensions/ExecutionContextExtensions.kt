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

package com.expediagroup.graphql.dataloader.instrumentation.extensions

import graphql.analysis.QueryTraverser
import graphql.analysis.QueryVisitorFieldEnvironment
import graphql.execution.ExecutionContext
import graphql.language.OperationDefinition
import kotlin.math.max

/**
 * Calculate the longest path of the [ExecutionContext] AST Document from the root node to a leaf node
 * @return the height of the AST Document
 */
internal fun ExecutionContext.getDocumentHeight(): Int {
    val getFieldDepth: (QueryVisitorFieldEnvironment?) -> Int = { queryVisitor ->
        var hasQueryVisitor = queryVisitor
        var height = 1
        while (hasQueryVisitor != null) {
            hasQueryVisitor = hasQueryVisitor.parentEnvironment
            height++
        }
        height
    }
    return QueryTraverser.Builder().schema(graphQLSchema).document(document).variables(coercedVariables.toMap()).build()
        .reducePreOrder(
            { queryVisitor, height -> max(getFieldDepth(queryVisitor.parentEnvironment), height) },
            0
        )
}

/**
 * Checks if the [ExecutionContext] is a [OperationDefinition.Operation.MUTATION]
 * @return Boolean indicating if GraphQL Operation is a Mutation
 */
internal fun ExecutionContext.isMutation(): Boolean =
    this.operationDefinition.operation == OperationDefinition.Operation.MUTATION
