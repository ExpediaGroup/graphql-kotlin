package com.expedia.graphql.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when trying to generate a class and cannot resolve the simple name.
 */
class CouldNotGetNameOfKClassException(kclass: KClass<*>)
    : GraphQLKotlinException("Could not get the name of the KClass $kclass")
