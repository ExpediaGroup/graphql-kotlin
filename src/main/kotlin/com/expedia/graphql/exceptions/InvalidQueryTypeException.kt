package com.expedia.graphql.exceptions

import kotlin.reflect.KClass

/**
 * Exception thrown on schema creation if any query class is not public.
 */
class InvalidQueryTypeException(klazz: KClass<*>)
    : GraphQLKotlinException("Schema requires all queries to be public, ${klazz.simpleName} query has ${klazz.visibility} visibility modifier")
