package com.expedia.graphql.schema.exceptions

import kotlin.reflect.KClass

class CouldNotGetNameOfEnumException(kclass: KClass<*>)
    : RuntimeException("Could not get the enum name of the KClass $kclass")
