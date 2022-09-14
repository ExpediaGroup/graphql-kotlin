package com.expediagroup.graphql.generator.exceptions

import graphql.GraphQLContext
import kotlin.reflect.KClass

/**
 * Thrown when [klazz] key was not found in a [GraphQLContext]
 */
class KeyNotFoundInGraphQLContextException(klazz: KClass<*>) : GraphQLKotlinException("GraphQLContext does not contain key ${klazz.simpleName}")
