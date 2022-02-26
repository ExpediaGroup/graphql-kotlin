package com.expediagroup.graphql.transactionbatcher.instrumentation.level.extensions

import graphql.analysis.QueryTraverser
import graphql.analysis.QueryVisitorFieldEnvironment
import graphql.execution.ExecutionContext
import kotlin.math.max

fun ExecutionContext.getDocumentHeight(): Int {
    val getFieldDepth: (QueryVisitorFieldEnvironment?) -> Int = { queryVisitor ->
        var hasQueryVisitor = queryVisitor
        var height = 1
        while (hasQueryVisitor != null) {
            hasQueryVisitor = hasQueryVisitor.parentEnvironment
            height++
        }
        height
    }
    return QueryTraverser.Builder().schema(graphQLSchema).document(document).variables(variables).build()
        .reducePreOrder(
            { queryVisitor, height -> max(getFieldDepth(queryVisitor.parentEnvironment), height) },
            0
        )
}
