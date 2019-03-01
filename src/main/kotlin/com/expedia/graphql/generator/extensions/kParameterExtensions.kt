package com.expedia.graphql.generator.extensions

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.exceptions.CouldNotCastArgumentException
import com.expedia.graphql.exceptions.CouldNotGetNameOfKParameterException
import graphql.schema.DataFetchingEnvironment
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaType

internal fun KParameter.isInterface() = this.type.getKClass().isInterface()

internal fun KParameter.isGraphQLContext() = this.findAnnotation<GraphQLContext>() != null

internal fun KParameter.isDataFetchingEnvironment() = this.type.classifier == DataFetchingEnvironment::class

@Throws(CouldNotGetNameOfKParameterException::class)
internal fun KParameter.getName(): String =
    this.name ?: throw CouldNotGetNameOfKParameterException(this)

@Throws(CouldNotCastArgumentException::class)
internal fun KParameter.javaTypeClass(): Class<*> =
    this.type.javaType as? Class<*> ?: throw CouldNotCastArgumentException(this)
