package com.expedia.graphql.exceptions

import kotlin.reflect.KClass

/**
 * Throws when the KClass is not one of the supported types for a GraphQLID
 */
class InvalidIdTypeException(kClass: KClass<*>, types: String)
    : GraphQLKotlinException("${kClass.simpleName} is not a valid ID type, only $types are accepted")
