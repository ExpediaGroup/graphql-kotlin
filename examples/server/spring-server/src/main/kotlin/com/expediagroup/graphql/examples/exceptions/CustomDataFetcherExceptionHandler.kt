/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.examples.exceptions

import com.expediagroup.graphql.server.exception.KotlinGraphQLError
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import graphql.ErrorType
import graphql.ErrorType.ValidationError
import graphql.ExceptionWhileDataFetching
import graphql.GraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import graphql.execution.ResultPath
import graphql.language.SourceLocation
import org.slf4j.LoggerFactory

class CustomDataFetcherExceptionHandler : DataFetcherExceptionHandler {
    private val log = LoggerFactory.getLogger(CustomDataFetcherExceptionHandler::class.java)

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.sourceLocation
        val path = handlerParameters.path

        val error: GraphQLError = when (exception) {
            is ValidationException -> ValidationDataFetchingGraphQLError(exception.constraintErrors, path, exception, sourceLocation)
            else -> KotlinGraphQLError(exception = exception, locations = listOf(sourceLocation), path = path.toList())
        }
        log.warn(error.message, exception)
        return DataFetcherExceptionHandlerResult.newResult().error(error).build()
    }
}

@JsonIgnoreProperties("exception")
class ValidationDataFetchingGraphQLError(
    val constraintErrors: List<ConstraintError>,
    path: ResultPath,
    exception: Throwable,
    sourceLocation: SourceLocation
) : ExceptionWhileDataFetching(
    path,
    exception,
    sourceLocation
) {
    override fun getErrorType(): ErrorType = ValidationError
}
