package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLID
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.full.findAnnotation

internal fun KAnnotatedElement.getGraphQLDescription(): String? = this.findAnnotation<GraphQLDescription>()?.value

internal fun KAnnotatedElement.getGraphQLName(): String? = this.findAnnotation<GraphQLName>()?.value

internal fun KAnnotatedElement.getDeprecationReason(): String? = this.findAnnotation<Deprecated>()?.getReason()

internal fun KAnnotatedElement.isGraphQLIgnored(): Boolean = this.findAnnotation<GraphQLIgnore>() != null

internal fun KAnnotatedElement.isGraphQLID(): Boolean = this.findAnnotation<GraphQLID>() != null

internal fun Deprecated.getReason(): String? {
    val builder = StringBuilder()
    builder.append(this.message)

    if (this.replaceWith.expression.isBlank().not()) {
        builder.append(", replace with ")
        builder.append(this.replaceWith.expression)
    }

    return builder.toString()
}
