package com.expediagroup.graphql.annotations

/**
 * Set the GraphQL name to be picked up by the schema generator.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class GraphQLName(val value: String)
