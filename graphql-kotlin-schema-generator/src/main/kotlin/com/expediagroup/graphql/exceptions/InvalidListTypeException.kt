package com.expediagroup.graphql.exceptions

import kotlin.reflect.KType

/**
 * Thrown on mapping an invalid list type
 */
class InvalidListTypeException(type: KType)
    : GraphQLKotlinException("Could not get the type of the first argument for the list $type")
