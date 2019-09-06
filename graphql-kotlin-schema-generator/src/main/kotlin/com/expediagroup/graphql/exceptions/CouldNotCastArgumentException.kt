package com.expediagroup.graphql.exceptions

import kotlin.reflect.KParameter

/**
 * Thrown when a KParameter could not be cast or mapped to arguments in the data fetcher
 */
class CouldNotCastArgumentException(kParameter: KParameter)
    : GraphQLKotlinException("Could not cast or map arguments in the data fetcher for $kParameter")
