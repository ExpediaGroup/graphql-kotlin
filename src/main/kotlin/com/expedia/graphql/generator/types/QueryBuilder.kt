package com.expedia.graphql.generator.types

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.exceptions.InvalidQueryTypeException
import com.expedia.graphql.exceptions.InvalidSchemaException
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getValidFunctions
import com.expedia.graphql.generator.extensions.isNotPublic
import graphql.schema.GraphQLObjectType

internal class QueryBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    @Throws(InvalidSchemaException::class)
    fun getQueryObject(queries: List<TopLevelObject>): GraphQLObjectType {

        if (queries.isEmpty()) {
            throw InvalidSchemaException()
        }

        val queryBuilder = GraphQLObjectType.Builder()
        queryBuilder.name(config.topLevelNames.query)

        for (query in queries) {
            if (query.kClass.isNotPublic()) {
                throw InvalidQueryTypeException(query.kClass)
            }

            generator.directives(query.kClass).forEach {
                queryBuilder.withDirective(it)
            }

            query.kClass.getValidFunctions(config.hooks)
                .forEach {
                    val function = generator.function(it, query.obj)
                    val functionFromHook = config.hooks.didGenerateQueryType(it, function)
                    queryBuilder.field(functionFromHook)
                }
        }

        return queryBuilder.build()
    }
}
