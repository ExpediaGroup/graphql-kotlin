package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.exceptions.InvalidQueryTypeException
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getValidFunctions
import com.expediagroup.graphql.generator.extensions.isNotPublic
import graphql.schema.GraphQLObjectType

internal class QueryBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    fun getQueryObject(queries: List<TopLevelObject>): GraphQLObjectType {
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
                    val function = generator.function(it, config.topLevelNames.query, query.obj)
                    val functionFromHook = config.hooks.didGenerateQueryType(it, function)
                    queryBuilder.field(functionFromHook)
                }
        }

        return queryBuilder.build()
    }
}
