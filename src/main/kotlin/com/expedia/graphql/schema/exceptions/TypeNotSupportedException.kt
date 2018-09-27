package com.expedia.graphql.schema.exceptions

/**
 * Thrown when the generator does not have a type to map to in GraphQL or in the hooks.
 */
class TypeNotSupportedException(typeName: String, packageName: String)
    : RuntimeException("Cannot convert $typeName since it is outside the supported package $packageName")
