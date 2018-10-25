package com.expedia.graphql.schema.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when the schema being generated has two classes with the same GraphQLType name,
 * but they are not the same Kotlin class. We can not have the full package or classpath info
 * in the GraphQLType so all names must be unique.
 */
class ConflictingTypesException(kClass1: KClass<*>, kClass2: KClass<*>)
    : GraphQLKotlinException("Conflicting class names in schema generation [$kClass1, $kClass2]")
