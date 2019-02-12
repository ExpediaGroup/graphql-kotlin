package com.expedia.graphql.exceptions

import kotlin.reflect.KParameter

class CouldNotCastArgumentException(kParameter: KParameter)
    : GraphQLKotlinException("Could not cast or map arguments in the data fetcher for $kParameter")
