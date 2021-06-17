package com.expediagroup.graphql.generator.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class GraphQLAbstractType(val possibleTypes: Array<KClass<*>>, val name: String, val description: String)
