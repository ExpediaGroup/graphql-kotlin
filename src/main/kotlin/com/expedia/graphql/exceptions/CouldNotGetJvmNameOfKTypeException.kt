package com.expedia.graphql.exceptions

import kotlin.reflect.KType

/**
 * Thrown when trying to generate a class and cannot resolve the jvm erasure name.
 */
class CouldNotGetJvmNameOfKTypeException(kType: KType?)
    : GraphQLKotlinException("Could not get the name of the KClass $kType")
