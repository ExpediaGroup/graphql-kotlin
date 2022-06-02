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

package com.expediagroup.graphql.dataloader.instrumentation.fixture

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.ProductDataLoader
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.ProductService
import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.ProductServiceRequest
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Product
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.ProductDetails
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.ProductSummary
import com.expediagroup.graphql.dataloader.instrumentation.level.state.ExecutionLevelDispatchedState
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExecutionExhaustedState
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.DataFetcher
import graphql.schema.SelectedField
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import io.mockk.spyk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

object ProductGraphQL {
    private val schema = """
        type Query {
            product(id: ID!): Product
            productSummary(productId: ID!): ProductSummary
            productDetails(productId: ID!): ProductDetails
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

    private val productService = ProductService()

    private val productDataFetcher = DataFetcher<CompletableFuture<Product>> { environment ->
        val productId = environment.getArgument<String>("id").toInt()
        val selectionFields = environment.selectionSet.immediateFields.map(SelectedField::getName)
        productService.getProduct(
            ProductServiceRequest(productId, selectionFields),
            environment
        )
    }

    private val productSummaryDataFetcher = DataFetcher<CompletableFuture<ProductSummary>> { environment ->
        val productId = environment.getArgument<String>("productId").toInt()
        val selectionFields = listOf("summary")
        productService.getProduct(
            ProductServiceRequest(productId, selectionFields),
            environment
        ).thenApply(Product::summary)
    }

    private val productDetailsDataFetcher = DataFetcher<CompletableFuture<ProductDetails>> { environment ->
        val productId = environment.getArgument<String>("productId").toInt()
        val selectionFields = listOf("details")
        productService.getProduct(
            ProductServiceRequest(productId, selectionFields),
            environment
        ).thenApply(Product::details)
    }

    private val runtimeWiring = RuntimeWiring.newRuntimeWiring().apply {
        type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("product", productDataFetcher)
                .dataFetcher("productSummary", productSummaryDataFetcher)
                .dataFetcher("productDetails", productDetailsDataFetcher)
        )
    }.build()

    val builder: GraphQL.Builder = GraphQL.newGraphQL(
        SchemaGenerator().makeExecutableSchema(
            SchemaParser().parse(schema),
            runtimeWiring
        )
    )

    fun execute(
        graphQL: GraphQL,
        queries: List<String>,
        dataLoaderInstrumentationStrategy: DataLoaderInstrumentationStrategy
    ): Pair<List<ExecutionResult>, KotlinDataLoaderRegistry> {
        val kotlinDataLoaderRegistry = spyk(
            KotlinDataLoaderRegistryFactory(
                ProductDataLoader()
            ).generate()
        )

        val graphQLContext = mapOf(
            when (dataLoaderInstrumentationStrategy) {
                DataLoaderInstrumentationStrategy.SYNC_EXHAUSTION ->
                    SyncExecutionExhaustedState::class to SyncExecutionExhaustedState(
                        queries.size,
                        kotlinDataLoaderRegistry
                    )
                DataLoaderInstrumentationStrategy.LEVEL_DISPATCHED ->
                    ExecutionLevelDispatchedState::class to ExecutionLevelDispatchedState(
                        queries.size
                    )
            }
        )

        val results = runBlocking {
            queries.map { query ->
                async {
                    graphQL.executeAsync(
                        ExecutionInput
                            .newExecutionInput(query)
                            .dataLoaderRegistry(kotlinDataLoaderRegistry)
                            .graphQLContext(graphQLContext)
                            .build()
                    ).await()
                }
            }.awaitAll()
        }

        return Pair(results, kotlinDataLoaderRegistry)
    }
}
