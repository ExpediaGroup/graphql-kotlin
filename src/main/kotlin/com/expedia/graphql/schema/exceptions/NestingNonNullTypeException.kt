package com.expedia.graphql.schema.exceptions

import graphql.schema.GraphQLType
import kotlin.reflect.KType

class NestingNonNullTypeException(gType: GraphQLType, kType: KType)
    : RuntimeException("Already non null, don't need to nest, $gType, $kType")
