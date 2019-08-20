package com.expedia.graphql.federation.exception

import graphql.ErrorClassification
import graphql.ErrorType
import graphql.GraphQLError
import graphql.language.SourceLocation

class FederatedRequestFailure(private val errorMessage: String, private val error: Exception) : GraphQLError {
    override fun getMessage(): String = errorMessage

    override fun getErrorType(): ErrorClassification = ErrorType.DataFetchingException

    override fun getLocations(): List<SourceLocation> = listOf(SourceLocation(-1, -1))

    override fun getExtensions(): Map<String, Any> = mapOf("error" to error)
}
