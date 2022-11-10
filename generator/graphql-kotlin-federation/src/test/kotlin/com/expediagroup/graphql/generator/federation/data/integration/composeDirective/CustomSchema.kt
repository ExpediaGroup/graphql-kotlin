package com.expediagroup.graphql.generator.federation.data.integration.composeDirective

import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.federation.directives.ComposeDirective
import graphql.introspection.Introspection

@ComposeDirective(name = "custom")
class CustomSchema

@GraphQLDirective(name = "custom", locations = [Introspection.DirectiveLocation.FIELD_DEFINITION])
annotation class CustomDirective

class SimpleQuery {
    @CustomDirective
    fun helloWorld(): String = "Hello World"
}
