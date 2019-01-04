package com.expedia.graphql.exceptions

/**
 * Thrown when the provided SchemaDirectiveWiring returns a null element
 */
class InvalidSchemaDirectiveWiringException(elementName: String)
    : GraphQLKotlinException("SchemaDirectiveWiring MUST return a non null return value for element $elementName")
