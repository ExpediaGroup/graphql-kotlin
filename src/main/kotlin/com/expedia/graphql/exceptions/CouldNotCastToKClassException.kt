package com.expedia.graphql.exceptions

import kotlin.reflect.KType

/**
 * Thrown if could not cast the KType.classifier to KClass
 */
class CouldNotCastToKClassException(kType: KType)
    : GraphQLKotlinException("Could not cast the KType.classifier to KClass. KType=$kType")
