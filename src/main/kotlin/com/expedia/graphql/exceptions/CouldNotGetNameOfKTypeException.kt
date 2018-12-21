package com.expedia.graphql.exceptions

import kotlin.reflect.KType

/**
 * Thrown when trying to generate a type and cannot resolve the simple name.
 */
class CouldNotGetNameOfKTypeException(kType: KType)
    : GraphQLKotlinException("Could not get the name of the KType $kType")
