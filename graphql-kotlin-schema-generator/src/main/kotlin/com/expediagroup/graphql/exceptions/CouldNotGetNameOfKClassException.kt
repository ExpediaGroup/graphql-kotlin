package com.expediagroup.graphql.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when trying to generate a class and cannot resolve the name.
 */
class CouldNotGetNameOfKClassException(kclass: KClass<*>)
    : GraphQLKotlinException("Could not get the name of the KClass $kclass")
