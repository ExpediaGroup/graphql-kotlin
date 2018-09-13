package com.expedia.graphql.annotations

/**
 * Meta annotation used to denote an annotation as a GraphQL
 * directive.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class GraphQLDirective(val name: String = "")
