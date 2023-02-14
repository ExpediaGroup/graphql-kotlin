package com.expediagroup.federation.compatibility

import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.federation.directives.ComposeDirective
import com.expediagroup.graphql.generator.federation.directives.LinkDirective
import com.expediagroup.graphql.server.Schema
import graphql.introspection.Introspection
import org.springframework.stereotype.Component

@LinkDirective(url = "https://myspecs.dev/myCustomDirective/v1.0", import = ["@custom"])
@ComposeDirective("@custom")
@Component
class CustomSchema : Schema

@GraphQLDirective(name = "custom", locations = [Introspection.DirectiveLocation.OBJECT])
annotation class CustomDirective
