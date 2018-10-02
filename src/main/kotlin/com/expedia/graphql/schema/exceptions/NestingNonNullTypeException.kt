package com.expedia.graphql.schema.exceptions

import graphql.schema.GraphQLType
import kotlin.reflect.KType

/**
 * Throws on nesting a non-null graphql type twice.
 */
class NestingNonNullTypeException(gType: GraphQLType, kType: KType)
    : RuntimeException("Already non null, don't need to nest, $gType, $kType")
