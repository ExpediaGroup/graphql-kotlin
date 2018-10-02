package com.expedia.graphql.annotations

import graphql.introspection.Introspection.DirectiveLocation
import graphql.introspection.Introspection.DirectiveLocation.FIELD
import graphql.introspection.Introspection.DirectiveLocation.FIELD_DEFINITION
import graphql.introspection.Introspection.DirectiveLocation.MUTATION
import graphql.introspection.Introspection.DirectiveLocation.OBJECT
import graphql.introspection.Introspection.DirectiveLocation.QUERY

/**
 * Meta annotation used to denote an annotation as a GraphQL directive.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class GraphQLDirective(
        val name: String = "",
        val description: String = "",
        val locations: Array<DirectiveLocation> = [QUERY, MUTATION, FIELD, FIELD_DEFINITION, OBJECT]
)
