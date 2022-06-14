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

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import graphql.schema.GraphQLSchema

/**
 * Entry point to generate a graphql schema using reflection on the passed objects.
 *
 * @param config schema generation configuration
 * @param queries list of [TopLevelObject] to use for GraphQL queries
 * @param mutations optional list of [TopLevelObject] to use for GraphQL mutations
 * @param subscriptions optional list of [TopLevelObject] to use for GraphQL subscriptions
 * @param schemaObject optional [TopLevelObject] reference to the annotated schema class
 *
 * @return GraphQLSchema from graphql-java
 */
@Throws(GraphQLKotlinException::class)
fun toSchema(
    config: SchemaGeneratorConfig,
    queries: List<TopLevelObject>,
    mutations: List<TopLevelObject> = emptyList(),
    subscriptions: List<TopLevelObject> = emptyList(),
    schemaObject: TopLevelObject? = null
): GraphQLSchema {
    val generator = SchemaGenerator(config)
    return generator.use {
        it.generateSchema(
            queries = queries,
            mutations = mutations,
            subscriptions = subscriptions,
            schemaObject = schemaObject
        )
    }
}
