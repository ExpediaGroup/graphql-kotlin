package com.expediagroup.graphql.client

data class GraphQLError(
    val message: String,
    val locations: List<SourceLocation> = emptyList(),
    val errorType: ErrorType = ErrorType.DataFetchingException,
    val path: List<Any>? = null,
    val extensions: Map<String, Any?>? = null
)

data class SourceLocation(
    val line: Int,
    val column: Int,
    val sourceName: String?
)

enum class ErrorType {
    InvalidSyntax,
    ValidationError,
    DataFetchingException,
    OperationNotSupported,
    ExecutionAborted,
    Unknown
}
