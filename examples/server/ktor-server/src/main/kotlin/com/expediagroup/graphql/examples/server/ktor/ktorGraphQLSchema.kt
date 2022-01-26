/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.examples.server.ktor

import com.expediagroup.graphql.examples.server.ktor.schema.BookQueryService
import com.expediagroup.graphql.examples.server.ktor.schema.CourseQueryService
import com.expediagroup.graphql.examples.server.ktor.schema.HelloQueryService
import com.expediagroup.graphql.examples.server.ktor.schema.LoginMutationService
import com.expediagroup.graphql.examples.server.ktor.schema.UniversityQueryService
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.toSchema
import graphql.GraphQL

/**
 * Custom logic for how this Ktor server loads all the queries and configuration to create the [GraphQL] object
 * needed to handle incoming requests. In a more enterprise solution you may want to load more things from
 * configuration files instead of hardcoding them.
 */
private val config = SchemaGeneratorConfig(supportedPackages = listOf("com.expediagroup.graphql.examples.server.ktor"))
private val queries = listOf(
    TopLevelObject(HelloQueryService()),
    TopLevelObject(BookQueryService()),
    TopLevelObject(CourseQueryService()),
    TopLevelObject(UniversityQueryService())
)
private val mutations = listOf(TopLevelObject(LoginMutationService()))
val graphQLSchema = toSchema(config, queries, mutations)

fun getGraphQLObject(): GraphQL = GraphQL.newGraphQL(graphQLSchema).build()
