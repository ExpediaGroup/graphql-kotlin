package com.expedia.graphql.generator.state

import com.expedia.graphql.directives.DeprecatedDirective
import graphql.Directives
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLType

internal class SchemaGeneratorState(supportedPackages: List<String>) {
    val cache = TypesCache(supportedPackages)
    val additionalTypes = mutableSetOf<GraphQLType>()
    val directives = mutableSetOf<GraphQLDirective>()

    fun getValidAdditionalTypes(): List<GraphQLType> = additionalTypes.filter { cache.doesNotContainGraphQLType(it) }

    init {
        // NOTE: GraphQLDirective does not implement hashCode/equals and graphql-java adds @include and @defer
        directives.add(Directives.IncludeDirective)
        directives.add(Directives.SkipDirective)
        // graphql-kotlin default
        directives.add(DeprecatedDirective)
    }
}
