package com.expedia.graphql.schema.extensions

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.exceptions.InvalidInputFieldTypeException
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure

@Throws(InvalidInputFieldTypeException::class)
internal fun KParameter.throwIfUnathorizedInterface() {
    if (this.type.jvmErasure.java.isInterface) throw InvalidInputFieldTypeException()
}

internal fun KParameter.isGraphQLContext() = this.findAnnotation<GraphQLContext>() != null
