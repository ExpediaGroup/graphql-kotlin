package com.expedia.graphql.generator.extensions

import com.expedia.graphql.exceptions.CouldNotCastGraphQLType
import com.expedia.graphql.exceptions.NestingNonNullTypeException
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition
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

@Throws(CouldNotCastGraphQLType::class)
internal inline fun <reified T : GraphQLType> GraphQLType.safeCast(): T {
    if (this !is T) throw CouldNotCastGraphQLType(this, T::class)
    return this
}

internal fun GraphQLDirectiveContainer.getAllDirectives(): List<GraphQLDirective> {
    // A function without directives may still be rewired if the arguments have directives
    // see https://github.com/ExpediaDotCom/graphql-kotlin/wiki/Schema-Directives for details
    val mutableList = mutableListOf<GraphQLDirective>()

    mutableList.addAll(this.directives)

    if (this is GraphQLFieldDefinition) {
        this.arguments.forEach { mutableList.addAll(it.directives) }
    }

    return mutableList
}
