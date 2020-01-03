/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.generator.types.generateMutations
import com.expediagroup.graphql.generator.types.generateQueries
import com.expediagroup.graphql.generator.types.generateSubscriptions
import graphql.schema.GraphQLSchema

/**
 * Internal use only. Please use [com.expediagroup.graphql.toSchema] instead.
 *
 * Generate a schema given some intial state and a list of objects to parse for the queries, mutations, and subscriptions.
 */
fun generateSchema(
    generator: SchemaGenerator,
    queries: List<TopLevelObject>,
    mutations: List<TopLevelObject> = emptyList(),
    subscriptions: List<TopLevelObject> = emptyList()
): GraphQLSchema {
    val builder = GraphQLSchema.newSchema()
    builder.query(generateQueries(generator, queries))
    builder.mutation(generateMutations(generator, mutations))
    builder.subscription(generateSubscriptions(generator, subscriptions))

    // add unreferenced interface implementations
    generator.additionalTypes.forEach {
        builder.additionalType(it)
    }

    builder.additionalDirectives(generator.directives.values.toSet())
    builder.codeRegistry(generator.codeRegistry.build())

    return generator.config.hooks.willBuildSchema(builder).build()
}
