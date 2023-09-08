package com.expediagroup.federation.compatibility

import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.federation.directives.ComposeDirective
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_LATEST_URL
import com.expediagroup.graphql.generator.federation.directives.LinkDirective
import com.expediagroup.graphql.generator.federation.directives.LinkImport
import com.expediagroup.graphql.generator.federation.directives.LinkedSpec
import com.expediagroup.graphql.server.Schema
import graphql.introspection.Introspection
import org.springframework.stereotype.Component

@LinkDirective(
    url = FEDERATION_SPEC_LATEST_URL,
    `as` = "federation",
    import = [
        LinkImport("@composeDirective"),
        LinkImport("@extends"),
        LinkImport("@external"),
        LinkImport("@inaccessible"),
        LinkImport("@interfaceObject"),
        LinkImport("@key"),
        LinkImport("@override"),
        LinkImport("@provides"),
        LinkImport("@requires"),
        LinkImport("@shareable"),
        LinkImport("@tag"),
        LinkImport("FieldSet")
    ]
)
@LinkDirective(url = "https://myspecs.dev/mySpec/v1.0", `as` = "mySpec", import = [LinkImport("@custom")])
@ComposeDirective("@custom")
@Component
class CustomSchema : Schema

@LinkedSpec("mySpec")
@GraphQLDirective(name = "custom", locations = [Introspection.DirectiveLocation.OBJECT])
annotation class CustomDirective
