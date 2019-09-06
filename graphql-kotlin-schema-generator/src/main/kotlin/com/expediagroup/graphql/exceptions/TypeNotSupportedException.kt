package com.expediagroup.graphql.exceptions

import kotlin.reflect.KType

/**
 * Thrown when the generator does not have a type to map to in GraphQL or in the hooks.
 */
class TypeNotSupportedException(kType: KType, packageList: List<String>)
    : GraphQLKotlinException("Cannot convert $kType since it is not a valid GraphQL type or outside the supported packages $packageList")
