package com.expediagroup.graphql.exceptions

import graphql.schema.GraphQLType
import kotlin.reflect.KType

/**
 * Throws on nesting a non-null graphql type twice.
 */
class NestingNonNullTypeException(gType: GraphQLType, kType: KType)
    : GraphQLKotlinException("Already non null, don't need to nest, $gType, $kType")
