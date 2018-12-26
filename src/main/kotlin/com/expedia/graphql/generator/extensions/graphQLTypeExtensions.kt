package com.expedia.graphql.generator.extensions

import com.expedia.graphql.exceptions.NestingNonNullTypeException
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLType
import kotlin.reflect.KType

/**
 * Map null and non-null types.
 * Throws an exception on wrapping a non-null graphql type twice.
 */
@Throws(NestingNonNullTypeException::class)
internal fun GraphQLType.wrapInNonNull(type: KType): GraphQLType = when {
    this is GraphQLNonNull -> throw NestingNonNullTypeException(this, type)
    type.isMarkedNullable -> this
    else -> GraphQLNonNull.nonNull(this)
}
