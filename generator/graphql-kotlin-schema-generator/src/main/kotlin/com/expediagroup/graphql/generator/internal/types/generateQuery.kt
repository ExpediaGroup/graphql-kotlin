/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.exceptions.ConflictingFieldsException
import com.expediagroup.graphql.generator.exceptions.InvalidQueryTypeException
import com.expediagroup.graphql.generator.internal.extensions.getValidFunctions
import com.expediagroup.graphql.generator.internal.extensions.isNotPublic
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLObjectType

internal fun generateQueries(generator: SchemaGenerator, queries: List<TopLevelObject>): GraphQLObjectType {
    val queryBuilder = GraphQLObjectType.Builder()
    queryBuilder.name(generator.config.topLevelNames.query)

    for (query in queries) {
        if (query.kClass.isNotPublic()) {
            throw InvalidQueryTypeException(query.kClass)
        }

        generateDirectives(generator, query.kClass, DirectiveLocation.OBJECT).forEach {
            queryBuilder.withAppliedDirective(it)
        }

        query.kClass.getValidFunctions(generator.config.hooks)
            .forEach {
                val function = generateFunction(generator, it, generator.config.topLevelNames.query, query.obj)
                val functionFromHook = generator.config.hooks.didGenerateQueryField(query.kClass, it, function)
                if (queryBuilder.hasField(functionFromHook.name)) {
                    throw ConflictingFieldsException("Query(class: ${query.kClass})", it.name)
                }
                queryBuilder.field(functionFromHook)
            }
    }

    return generator.config.hooks.didGenerateQueryObject(queryBuilder.build())
}
