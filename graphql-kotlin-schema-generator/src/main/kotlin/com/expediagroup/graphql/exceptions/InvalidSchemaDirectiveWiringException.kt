package com.expediagroup.graphql.exceptions

/**
 * Thrown when the provided SchemaDirectiveWiring returns a null element
 */
class InvalidSchemaDirectiveWiringException(reason: String)
    : GraphQLKotlinException(reason)
