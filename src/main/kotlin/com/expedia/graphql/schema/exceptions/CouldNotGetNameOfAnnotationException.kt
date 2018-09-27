package com.expedia.graphql.schema.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when unable to get the annotaiton name of a KAnnotatedElement.
 */
class CouldNotGetNameOfAnnotationException(kClass: KClass<*>)
    : RuntimeException("Could not get name of annotation class $kClass")
