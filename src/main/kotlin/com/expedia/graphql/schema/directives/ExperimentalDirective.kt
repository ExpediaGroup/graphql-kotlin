package com.expedia.graphql.schema.directives

import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective

val ExperimentalDirective: GraphQLDirective = GraphQLDirective.newDirective()
        .name("experimental")
        .description("Directs the GraphQL schema that target feature is marked as experimental and can be changed without notice")
        .validLocations(
                Introspection.DirectiveLocation.QUERY,
                Introspection.DirectiveLocation.MUTATION,
                Introspection.DirectiveLocation.FIELD,
                Introspection.DirectiveLocation.FIELD_DEFINITION,
                Introspection.DirectiveLocation.OBJECT
                )
        .build()