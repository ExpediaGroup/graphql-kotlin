package com.expedia.graphql.annotations

/**
 * Set the GraphQL name to be picked up by the schema generator.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
annotation class GraphQLName(val value: String)
