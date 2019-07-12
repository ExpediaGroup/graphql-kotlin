package com.expedia.graphql.exceptions

import kotlin.reflect.KParameter

/**
 * Thrown when trying to generate a parameter and cannot resolve the name.
 */
class CouldNotGetNameOfKParameterException(kParameter: KParameter)
    : GraphQLKotlinException("Could not get name of the KParameter $kParameter")
