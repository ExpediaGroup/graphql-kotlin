/*
 * Copyright 2019 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
