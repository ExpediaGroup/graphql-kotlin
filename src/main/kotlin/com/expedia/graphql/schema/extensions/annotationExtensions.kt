package com.expedia.graphql.schema.extensions

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLID
import com.expedia.graphql.annotations.GraphQLIgnore
import java.lang.reflect.Field
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.full.findAnnotation

internal fun KAnnotatedElement.graphQLDescription(): String? = this.findAnnotation<GraphQLDescription>()?.value

internal fun Field.graphQLDescription(): String? = this.getAnnotation(GraphQLDescription::class.java)?.value

internal fun KAnnotatedElement.getDeprecationReason(): String? = this.findAnnotation<Deprecated>()?.getReason()

internal fun Field.getDeprecationReason(): String? = this.getDeclaredAnnotation(Deprecated::class.java)?.getReason()

private fun Deprecated.getReason(): String? {
    val builder = StringBuilder()
    builder.append(this.message)

    if (this.replaceWith.expression.isBlank().not()) {
        builder.append(", replace with ")
        builder.append(this.replaceWith.expression)
    }

    return builder.toString()
}

internal fun KAnnotatedElement.isGraphQLIgnored() = this.findAnnotation<GraphQLIgnore>() != null

internal fun KAnnotatedElement.isGraphQLID() = this.findAnnotation<GraphQLID>() != null
