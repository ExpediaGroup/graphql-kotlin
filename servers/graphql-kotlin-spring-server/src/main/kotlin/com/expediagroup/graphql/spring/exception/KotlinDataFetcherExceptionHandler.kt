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

package com.expediagroup.graphql.spring.exception

import com.expediagroup.graphql.server.exception.KotlinGraphQLError
import graphql.GraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.slf4j.LoggerFactory

/**
 * Default DataFetcherExceptionHandler used by all GraphQL execution strategies. All exceptions thrown during GraphQL execution are logged and then returned as wrapped
 * ExceptionWhileDataFetching error if they are not a valid GraphQLError
 */
class KotlinDataFetcherExceptionHandler : DataFetcherExceptionHandler {
    private val logger = LoggerFactory.getLogger(KotlinDataFetcherExceptionHandler::class.java)

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val exception: Throwable = handlerParameters.exception
        val sourceLocation = handlerParameters.sourceLocation
        val path = handlerParameters.path

        val error: GraphQLError = KotlinGraphQLError(exception = exception, locations = listOf(sourceLocation), path = path.toList())

        logger.warn(error.message, exception)

        return DataFetcherExceptionHandlerResult.newResult(error).build()
    }
}
