package com.expediagroup.graphql.sample.exceptions

import graphql.ErrorClassification
import graphql.ErrorType
import graphql.GraphQLError
import graphql.language.SourceLocation

open class SimpleKotlinGraphQLError(
    private val exception: Throwable,
    private val errorType: ErrorType
    ) : GraphQLError {
    override fun getErrorType(): ErrorClassification = errorType

    override fun getLocations(): List<SourceLocation> = emptyList()

    override fun getMessage(): String = "Exception while running code outside of data handler: ${exception.message}"

    override fun getExtensions(): Map<String, Any> {
        val newExtensions = mutableMapOf<String, Any>()
        if (exception is GraphQLError && exception.extensions != null) {
            newExtensions.putAll(exception.extensions)
        }
        return newExtensions
    }
}
