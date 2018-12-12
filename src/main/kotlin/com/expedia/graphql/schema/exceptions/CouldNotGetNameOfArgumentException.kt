package com.expedia.graphql.schema.exceptions

import kotlin.reflect.KParameter

/**
 * Thrown when unable to get the simple name of a function argument
 */
class CouldNotGetNameOfArgumentException(kParameter: KParameter)
    : GraphQLKotlinException("Could not get name of parameter $kParameter")
