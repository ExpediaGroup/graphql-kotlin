package com.expediagroup.graphql.apq.fixture

import com.expediagroup.graphql.apq.InMemoryAutomaticPersistedQueryCache
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.execution.preparsed.persisted.ApolloPersistedQuerySupport
import graphql.schema.DataFetcher
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking

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

    private val productDataFetcher = DataFetcher<Product?> { environment ->
        val productId = environment.getArgument<String>("id").toInt()
        products[productId]
    }

    private val runtimeWiring = RuntimeWiring.newRuntimeWiring().apply {
        type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("product", productDataFetcher)
        )
    }.build()

    private val graphQL = GraphQL
        .newGraphQL(SchemaGenerator().makeExecutableSchema(SchemaParser().parse(schema), runtimeWiring))
        .preparsedDocumentProvider(ApolloPersistedQuerySupport(InMemoryAutomaticPersistedQueryCache()))
        .build()

    fun execute(executionInput: ExecutionInput): ExecutionResult =
        runBlocking {
            graphQL.executeAsync(executionInput).await()
        }
}
