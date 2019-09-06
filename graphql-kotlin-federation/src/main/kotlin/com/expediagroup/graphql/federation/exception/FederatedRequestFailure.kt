package com.expediagroup.graphql.federation.exception

import graphql.ErrorClassification
import graphql.ErrorType
import graphql.GraphQLError
import graphql.language.SourceLocation

/**
 * GraphQLError returned whenever exception occurs while trying to resolve federated entity representation.
 */
class FederatedRequestFailure(private val errorMessage: String, private val error: Exception? = null) : GraphQLError {
    override fun getMessage(): String = errorMessage

    override fun getErrorType(): ErrorClassification = ErrorType.DataFetchingException

    override fun getLocations(): List<SourceLocation> = listOf(SourceLocation(-1, -1))

    override fun getExtensions(): Map<String, Any>? =
        if (error != null) {
            mapOf("error" to error)
        } else {
            null
        }
}
