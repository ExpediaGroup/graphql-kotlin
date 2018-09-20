package com.expedia.graphql.schema.exceptions

import kotlin.reflect.KClass

class CouldNotGetNameOfAnnotationException(kClass: KClass<*>)
    : RuntimeException("Could not get name of annotation class $kClass")
