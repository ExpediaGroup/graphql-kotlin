package com.expedia.graphql.generator.extensions

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.exceptions.CouldNotGetNameOfKParameterException
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

internal fun KParameter.isInterface() = this.type.getKClass().isInterface()

internal fun KParameter.isGraphQLContext() = this.findAnnotation<GraphQLContext>() != null

@Throws(CouldNotGetNameOfKParameterException::class)
internal fun KParameter.getName(): String =
    this.name ?: throw CouldNotGetNameOfKParameterException(this)
