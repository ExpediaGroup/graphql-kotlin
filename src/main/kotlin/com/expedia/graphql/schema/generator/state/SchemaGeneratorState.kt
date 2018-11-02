package com.expedia.graphql.schema.generator.state

import com.expedia.graphql.schema.generator.TypesCache
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLType

internal class SchemaGeneratorState(supportedPackages: List<String>) {
    val cache = TypesCache(supportedPackages)
    val additionTypes = mutableSetOf<GraphQLType>()
    val directives = mutableSetOf<GraphQLDirective>()

    fun getValidAdditionTypes(): List<GraphQLType> = additionTypes.filter { cache.doesNotContainGraphQLType(it) }
}
