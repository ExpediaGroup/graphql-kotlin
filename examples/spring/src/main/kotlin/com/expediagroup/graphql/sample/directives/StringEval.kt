package com.expediagroup.graphql.sample.directives

import com.expediagroup.graphql.annotations.GraphQLDirective
import graphql.introspection.Introspection.DirectiveLocation

@GraphQLDirective(
        description = "This validates string input",
        locations = [DirectiveLocation.FIELD, DirectiveLocation.FIELD_DEFINITION, DirectiveLocation.ARGUMENT_DEFINITION])
annotation class StringEval(
        val default: String = "",
        val lowerCase: Boolean = false
)
