package com.expedia.graphql.exceptions

/**
 * Exception thrown on schema creation if no queries are specified.
 */
class InvalidSchemaException : GraphQLKotlinException("Schema requires at least one query")
