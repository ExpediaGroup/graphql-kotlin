package com.expedia.graphql.sample.query

import graphql.ExceptionWhileDataFetching
import graphql.execution.DataFetcherResult
import graphql.execution.ExecutionPath
import graphql.language.SourceLocation
import org.springframework.stereotype.Component

@Component
class DataAndErrors : Query {

    fun returnDataAndErrors(): DataFetcherResult<String> {
        val error = ExceptionWhileDataFetching(ExecutionPath.rootPath(), RuntimeException(), SourceLocation(1, 1))
        return DataFetcherResult("Hello from data fetcher", listOf(error))
    }
}
