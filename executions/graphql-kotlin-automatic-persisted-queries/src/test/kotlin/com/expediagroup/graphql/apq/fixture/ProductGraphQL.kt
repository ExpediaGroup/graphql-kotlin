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

package com.expediagroup.graphql.apq.fixture

import com.expediagroup.graphql.apq.cache.DefaultAutomaticPersistedQueriesCache
import com.expediagroup.graphql.apq.provider.AutomaticPersistedQueriesProvider
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring

object ProductGraphQL {

    private val products = listOf(
        Product(1, ProductSummary("Product 1"), ProductDetails("4 out of 5")),
        Product(2, ProductSummary("Product 2"), ProductDetails("3 out of 5"))
    ).associateBy(Product::id)

    private val schema = """
        type Query {
            product(id: ID!): Product
        }
        type Product {
            summary: ProductSummary
            details: ProductDetails
        }
        type ProductSummary {
            name: String!
        }
        type ProductDetails {
            rating: String!
        }
    """.trimIndent()

    private val runtimeWiring = RuntimeWiring.newRuntimeWiring().apply {
        type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("product") { products[it.getArgument<String>("id").toInt()] }
        )
    }.build()

    private val automaticPersistedQueriesProvider = AutomaticPersistedQueriesProvider(DefaultAutomaticPersistedQueriesCache())
    private var graphQL = GraphQL
        .newGraphQL(SchemaGenerator().makeExecutableSchema(SchemaParser().parse(schema), runtimeWiring))
        .preparsedDocumentProvider(automaticPersistedQueriesProvider)
        .build()

    fun clearCache() {
        graphQL = graphQL.transform {
            it.preparsedDocumentProvider(AutomaticPersistedQueriesProvider(DefaultAutomaticPersistedQueriesCache()))
        }
    }

    fun execute(
        executionInput: ExecutionInput
    ): ExecutionResult {
        return graphQL.executeAsync(executionInput).get()
    }

    fun executeAndReturnProvider(
        executionInput: ExecutionInput
    ): Pair<ExecutionResult, AutomaticPersistedQueriesProvider> =
        Pair(graphQL.executeAsync(executionInput).get(), automaticPersistedQueriesProvider)
}
