package com.expedia.graphql.exceptions

/**
 * Base exception that all our library exceptions extend from.
 */
open class GraphQLKotlinException(message: String = "", throwable: Throwable? = null)
    : RuntimeException(message, throwable)
