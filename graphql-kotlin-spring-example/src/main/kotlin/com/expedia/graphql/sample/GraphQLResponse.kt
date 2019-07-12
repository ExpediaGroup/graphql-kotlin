package com.expedia.graphql.sample

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import graphql.ExecutionResult

@JsonInclude(Include.NON_NULL)
data class GraphQLResponse(
    val data: Any? = null,
    val errors: List<Any>? = null,
    val extensions: Map<Any, Any>? = null
)

/**
 * Convert ExecutionResult to GraphQLResponse.
 *
 * NOTE: we need this as graphql-java defaults to only serializing GraphQLError objects so any custom error fields are ignored.
 */
internal fun ExecutionResult.toGraphQLResponse(): GraphQLResponse {
    val filteredErrors = if (errors?.isNotEmpty() == true) errors else null
    val filteredExtensions = if (extensions?.isNotEmpty() == true) extensions else null
    return GraphQLResponse(getData(), filteredErrors, filteredExtensions)
}
