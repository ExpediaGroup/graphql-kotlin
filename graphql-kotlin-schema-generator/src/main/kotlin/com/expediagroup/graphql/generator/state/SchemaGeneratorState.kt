package com.expediagroup.graphql.generator.state

import com.expediagroup.graphql.directives.DeprecatedDirective
import graphql.Directives
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLType
import java.util.concurrent.ConcurrentHashMap

@Suppress("UseDataClass")
internal class SchemaGeneratorState(supportedPackages: List<String>) {
    val cache = TypesCache(supportedPackages)
    val additionalTypes = mutableSetOf<GraphQLType>()
    val directives = ConcurrentHashMap<String, GraphQLDirective>()

    init {
        // NOTE: @include and @defer query directives are added by graphql-java by default
        // adding them explicitly here to keep it consistent with missing deprecated directive
        directives[Directives.IncludeDirective.name] = Directives.IncludeDirective
        directives[Directives.SkipDirective.name] = Directives.SkipDirective

        // graphql-kotlin default directives
        // @deprecated directive is a built-in directive that each GraphQL server should provide bu currently it is not added by graphql-java
        //   see https://github.com/graphql-java/graphql-java/issues/1598
        directives[DeprecatedDirective.name] = DeprecatedDirective
    }
}
