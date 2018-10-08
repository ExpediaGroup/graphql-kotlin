package com.expedia.graphql.schema.extensions

import com.expedia.graphql.schema.exceptions.NestingNonNullTypeException
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLType
import kotlin.reflect.KType

/**
 * Renders a readable string from the given graphql type no matter how deeply nested
 * Eg: [[Int!]]!
 */
val GraphQLType.deepName: String
    get() = when {
        this is GraphQLNonNull -> "${this.wrappedType.deepName}!"
        this is GraphQLList -> "[${this.wrappedType.deepName}]"
        else -> name
    }

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
