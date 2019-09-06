package com.expediagroup.graphql.exceptions

import kotlin.reflect.KClass

/**
 * Exception thrown on schema creation if any mutation class is not public.
 */
class InvalidMutationTypeException(klazz: KClass<*>) : GraphQLKotlinException("Schema requires all mutations to be public, ${klazz.simpleName} mutation has ${klazz.visibility} visibility modifier")
