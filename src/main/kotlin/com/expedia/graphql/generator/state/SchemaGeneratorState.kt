package com.expedia.graphql.generator.state

import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLType

internal class SchemaGeneratorState(supportedPackages: List<String>) {
    val cache = TypesCache(supportedPackages)
    val additionalTypes = mutableSetOf<GraphQLType>()
    val directives = mutableSetOf<GraphQLDirective>()

    fun getValidAdditionalTypes(): List<GraphQLType> = additionalTypes.filter { cache.doesNotContainGraphQLType(it) }
}
