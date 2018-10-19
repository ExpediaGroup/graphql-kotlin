package com.expedia.graphql.schema.extensions

import com.expedia.graphql.schema.exceptions.InvalidListTypeException
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal fun KType.graphQLDescription(): String? = (classifier as? KClass<*>)?.graphQLDescription()

@Throws(InvalidListTypeException::class)
internal fun KType.getTypeOfFirstArgument(): KType =
    this.arguments.first().type ?: throw InvalidListTypeException(this)
