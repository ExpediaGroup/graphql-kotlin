package com.expediagroup.graphql.exceptions

import graphql.schema.GraphQLType
import kotlin.reflect.KClass

/**
 * Thrown when the casting a GraphQLType to some parent type is invalid
 */
class CouldNotCastGraphQLType(type: GraphQLType, kClass: KClass<*>)
    : GraphQLKotlinException("Could not cast GraphQLType $type to $kClass")
