package com.expedia.graphql.annotations

import graphql.introspection.Introspection.DirectiveLocation
import graphql.introspection.Introspection.DirectiveLocation.ARGUMENT_DEFINITION
import graphql.introspection.Introspection.DirectiveLocation.ENUM
import graphql.introspection.Introspection.DirectiveLocation.ENUM_VALUE
import graphql.introspection.Introspection.DirectiveLocation.FIELD_DEFINITION
import graphql.introspection.Introspection.DirectiveLocation.INPUT_FIELD_DEFINITION
import graphql.introspection.Introspection.DirectiveLocation.INPUT_OBJECT
import graphql.introspection.Introspection.DirectiveLocation.INTERFACE
import graphql.introspection.Introspection.DirectiveLocation.OBJECT
import graphql.introspection.Introspection.DirectiveLocation.SCALAR
import graphql.introspection.Introspection.DirectiveLocation.SCHEMA
import graphql.introspection.Introspection.DirectiveLocation.UNION

/**
 * Meta annotation used to denote an annotation as a GraphQL schema directives.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class GraphQLDirective(
    val name: String = "",
    val description: String = "",
    val locations: Array<DirectiveLocation> = [
        SCHEMA,
        SCALAR,
        OBJECT,
        FIELD_DEFINITION,
        ARGUMENT_DEFINITION,
        INTERFACE,
        UNION,
        ENUM,
        ENUM_VALUE,
        INPUT_OBJECT,
        INPUT_FIELD_DEFINITION
    ]
)
