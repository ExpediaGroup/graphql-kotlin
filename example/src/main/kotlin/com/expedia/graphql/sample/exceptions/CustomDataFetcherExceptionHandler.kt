package com.expedia.graphql.sample.exceptions

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import graphql.ErrorType
import graphql.ErrorType.ValidationError
import graphql.ExceptionWhileDataFetching
import graphql.GraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.ExecutionPath
import graphql.language.SourceLocation
import org.slf4j.LoggerFactory

class CustomDataFetcherExceptionHandler : DataFetcherExceptionHandler {
    private val log = LoggerFactory.getLogger(CustomDataFetcherExceptionHandler::class.java)

    override fun accept(handlerParameters: DataFetcherExceptionHandlerParameters) {
        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.field.sourceLocation
        val path = handlerParameters.path

        val error: GraphQLError = when(exception) {
            is ValidationException -> ValidationDataFetchingGraphQLError(exception.constraintErrors, path, exception, sourceLocation)
            else -> ExceptionWhileDataFetching(path, exception, sourceLocation)
        }

        handlerParameters.executionContext.addError(error, path)
        log.warn(error.message, exception)
    }
}

@JsonIgnoreProperties("exception")
class ValidationDataFetchingGraphQLError(
        val constraintErrors: List<ConstraintError>,
        path: ExecutionPath,
        exception: Throwable,
        sourceLocation: SourceLocation
) : ExceptionWhileDataFetching(
        path,
        exception,
        sourceLocation
) {
    override fun getErrorType(): ErrorType = ValidationError
}