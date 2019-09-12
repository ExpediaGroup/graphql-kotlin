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

package com.expediagroup.graphql.boot.server.exception

import graphql.ExceptionWhileDataFetching
import graphql.GraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.slf4j.LoggerFactory

/**
 * Default [DataFetcherExceptionHandler] used by all GraphQL execution strategies. All exceptions thrown during GraphQL execution are logged and then returned as wrapped
 * [ExceptionWhileDataFetching] error.
 */
class KotlinDataFetcherExceptionHandler : DataFetcherExceptionHandler {
    private val logger = LoggerFactory.getLogger(KotlinDataFetcherExceptionHandler::class.java)

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.sourceLocation
        val path = handlerParameters.path

        val error: GraphQLError = ExceptionWhileDataFetching(path, exception, sourceLocation)
        logger.warn(error.message, exception)
        return DataFetcherExceptionHandlerResult.newResult(error).build()
    }
}
