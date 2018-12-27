package com.expedia.graphql.generator.extensions

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.exceptions.CouldNotGetNameOfKParameterException
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure

internal fun KParameter.isInterface() = this.type.jvmErasure.isInterface()

internal fun KParameter.isGraphQLContext() = this.findAnnotation<GraphQLContext>() != null

internal fun KParameter.getParamterGraphQLDescription() = this.getGraphQLDescription() ?: this.type.getKClass().getGraphQLDescription()

@Throws(CouldNotGetNameOfKParameterException::class)
internal fun KParameter.getName(): String =
    this.name ?: throw CouldNotGetNameOfKParameterException(this)
