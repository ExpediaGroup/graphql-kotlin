package com.expedia.graphql.exceptions

import kotlin.reflect.KParameter

/**
 * Thrown when unable to get the simple name of a function argument
 */
class CouldNotGetNameOfKParameterException(kParameter: KParameter)
    : GraphQLKotlinException("Could not get name of the KParameter $kParameter")
