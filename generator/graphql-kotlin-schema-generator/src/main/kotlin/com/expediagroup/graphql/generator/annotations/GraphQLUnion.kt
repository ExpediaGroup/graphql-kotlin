package com.expediagroup.graphql.generator.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class GraphQLUnion(
    val name: String,
    val possibleTypes: Array<KClass<*>>,
    val description: String = ""
)
