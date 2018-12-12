package com.expedia.graphql.schema.exceptions

import kotlin.reflect.KType

class CouldNotCastToKClassException(kType: KType)
    : GraphQLKotlinException("Could not cast the KType.classifier to KClass. KType=$kType")
