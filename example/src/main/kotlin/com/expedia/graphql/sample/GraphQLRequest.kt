package com.expedia.graphql.sample

import com.expedia.graphql.sample.context.MyGraphQLContext
import graphql.ExecutionInput

data class GraphQLRequest(
    val query: String,
    val operationName: String? = null,
    val variables: Map<String, Any>? = null
)

fun GraphQLRequest.getExecutionInput(graphQLContext: MyGraphQLContext? = null): ExecutionInput =
    ExecutionInput.newExecutionInput()
        .query(this.query)
        .operationName(this.operationName)
        .variables(this.variables)
        .context(graphQLContext)
        .build()
