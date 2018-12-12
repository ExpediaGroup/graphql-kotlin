package com.expedia.graphql.schema.exceptions

import kotlin.reflect.KClass

class InvalidIdTypeException(kClass: KClass<*>, types: String)
    : GraphQLKotlinException("${kClass.simpleName} is not a valid ID type, only $types are accepted")
