package com.expediagroup.graphql.directives

import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective

const val DEPRECATED_DIRECTIVE_NAME = "deprecated"

private val DefaultDeprecatedArgument: GraphQLArgument = GraphQLArgument.newArgument()
    .name("reason")
    .type(Scalars.GraphQLString)
    .defaultValue("No longer supported")
    .build()

internal val DeprecatedDirective: GraphQLDirective = GraphQLDirective.newDirective()
    .name(DEPRECATED_DIRECTIVE_NAME)
    .description("Marks the target field/enum value as deprecated")
    .argument(DefaultDeprecatedArgument)
    .validLocations(Introspection.DirectiveLocation.FIELD_DEFINITION, Introspection.DirectiveLocation.ENUM_VALUE)
    .build()

internal fun deprecatedDirectiveWithReason(reason: String): GraphQLDirective = DeprecatedDirective.transform { directive ->
    directive.argument(DefaultDeprecatedArgument.transform { arg ->
        arg.value(reason)
    })
}
