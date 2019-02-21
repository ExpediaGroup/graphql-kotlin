package com.expedia.graphql.generator.extensions

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.exceptions.CouldNotGetNameOfKParameterException
import graphql.schema.DataFetchingEnvironment
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSuperclassOf

internal fun KParameter.isInterface() = this.type.getKClass().isInterface()

internal fun KParameter.isGraphQLContext() = this.findAnnotation<GraphQLContext>() != null

internal fun KParameter.isDataFetchingEnvironment() = this.type.getKClass().isSuperclassOf(DataFetchingEnvironment::class)

@Throws(CouldNotGetNameOfKParameterException::class)
internal fun KParameter.getName(): String =
    this.name ?: throw CouldNotGetNameOfKParameterException(this)
