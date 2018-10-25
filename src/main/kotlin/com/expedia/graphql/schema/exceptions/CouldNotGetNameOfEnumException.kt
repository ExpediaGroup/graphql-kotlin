package com.expedia.graphql.schema.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when trying to generate an enum class and cannot resolve the simple name.
 */
class CouldNotGetNameOfEnumException(kclass: KClass<*>)
    : GraphQLKotlinException("Could not get the enum name of the KClass $kclass")
