package com.expedia.graphql.sample.directives

import com.expedia.graphql.annotations.GraphQLDirective
import graphql.introspection.Introspection.DirectiveLocation.FIELD

@GraphQLDirective(
        name = "CustomMarkup",
        description = "Add markup to a field",
        locations = [FIELD]
)
annotation class CustomDirective