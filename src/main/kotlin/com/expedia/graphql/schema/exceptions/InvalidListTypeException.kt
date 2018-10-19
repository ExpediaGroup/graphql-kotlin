package com.expedia.graphql.schema.exceptions

import kotlin.reflect.KType

/**
 * Thrown on mapping an invalid list type
 */
class InvalidListTypeException(type: KType)
    : RuntimeException("Could not get the type of the first argument for the list $type")
