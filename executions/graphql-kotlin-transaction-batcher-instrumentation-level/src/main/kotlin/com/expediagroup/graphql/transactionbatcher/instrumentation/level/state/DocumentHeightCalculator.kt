package com.expediagroup.graphql.transactionbatcher.instrumentation.level.state

import graphql.analysis.QueryTraverser
import graphql.analysis.QueryVisitorFieldEnvironment
import graphql.execution.ExecutionContext
import kotlin.math.max

internal object DocumentHeightCalculator {
    fun calculate(executionContext: ExecutionContext): Int {
        val queryTraverser = QueryTraverser.Builder()
            .schema(executionContext.graphQLSchema)
            .document(executionContext.document)
            .operationName(executionContext.executionInput.operationName)
            .variables(executionContext.variables)
            .build()

        val getPathDepth: (QueryVisitorFieldEnvironment?) -> Int = {
            var path: QueryVisitorFieldEnvironment? = it
            var length = 1
            while (path != null) {
                path = path.parentEnvironment
                length++
            }
            length
        }
        return queryTraverser.reducePreOrder(
            { env, acc -> max(getPathDepth(env.parentEnvironment), acc) },
            0
        )
    }
}
