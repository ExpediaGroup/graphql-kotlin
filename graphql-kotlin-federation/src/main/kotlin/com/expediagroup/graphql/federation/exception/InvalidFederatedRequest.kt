package com.expediagroup.graphql.federation.exception

import graphql.ErrorClassification
import graphql.ErrorType
import graphql.GraphQLError
import graphql.language.SourceLocation

/**
 * GraphQLError returned when federated representation cannot be resolved by any type resolvers.
 */
class InvalidFederatedRequest(private val errorMessage: String) : GraphQLError {
    override fun getMessage(): String = errorMessage

    override fun getErrorType(): ErrorClassification = ErrorType.ValidationError

    override fun getLocations(): List<SourceLocation> = listOf(SourceLocation(-1, -1))
}
