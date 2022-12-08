package com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExecutionExhaustedState
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

interface Item {
    val id: String
    val syncScalar: String
}

data class ItemImpl(override val id: String, override val syncScalar: String) : Item
data class ItemMessage(val value: String)

class IssueTest {
    private val schema = """
        type Query {
            item(id: ID!): Item!
        }
        type Item {
            id: ID!
            syncScalar: String!       # this field can be resolved from property
            asyncScalar: String!      # this field has an async data fetcher
            asyncObject: ItemMessage! # this fields has an async data fetcher
        }
        type ItemMessage {
            value: String!
        }
    """.trimIndent()

    private val runtimeWiring = RuntimeWiring.newRuntimeWiring().apply {
        type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("item") { env ->
                    val itemId = env.getArgument<String>("id")
                    CompletableFuture.completedFuture(
                        ItemImpl(itemId, "sync scalar: $itemId")
                    )
                }
        )
        type(
            TypeRuntimeWiring.newTypeWiring("Item")
                .dataFetcher("asyncScalar") { env ->
                    val item = env.getSource<Item>()
                    val loader = env.getDataLoader<String, String>("asyncScalar")
                    loader.load(item.id)
                }
                .dataFetcher("asyncObject") { env ->
                    val item = env.getSource<Item>()
                    val loader = env.getDataLoader<String, ItemMessage>("asyncObject")
                    loader.load(item.id)
                }
        )
    }.build()

    private val scalarDataLoader = object : KotlinDataLoader<String, String> {
        override val dataLoaderName: String = "asyncScalar"
        override fun getDataLoader(): DataLoader<String, String> =
            DataLoaderFactory.newDataLoader { keys ->
                CompletableFuture.completedFuture(
                    keys.map {
                        "detail of item $it"
                    }
                )
            }
    }

    private val objectDataLoader = object : KotlinDataLoader<String, ItemMessage> {
        override val dataLoaderName: String = "asyncObject"
        override fun getDataLoader(): DataLoader<String, ItemMessage> =
            DataLoaderFactory.newDataLoader { keys ->
                CompletableFuture.completedFuture(
                    keys.map {
                        ItemMessage("message of item $it")
                    }
                )
            }

    }

    val graphQL: GraphQL = GraphQL
        .newGraphQL(SchemaGenerator().makeExecutableSchema(SchemaParser().parse(schema), runtimeWiring))
        //.instrumentation(DataLoaderLevelDispatchedInstrumentation())
        .instrumentation(DataLoaderSyncExecutionExhaustedInstrumentation())
        .doNotAddDefaultInstrumentations()
        .build()

    @Test
    fun test() {
        val query = """
            query Sample {
                item(id: "1") {
                    id
                    syncScalar
                    asyncScalar
                    asyncObject {
                        value
                    }
                }
            }
        """.trimIndent()

        val dataLoaderRegistry = KotlinDataLoaderRegistryFactory(
            scalarDataLoader, objectDataLoader
        ).generate()

        val input = ExecutionInput.newExecutionInput(query)
            .dataLoaderRegistry(dataLoaderRegistry)
            .graphQLContext(mapOf(
                //ExecutionLevelDispatchedState::class to ExecutionLevelDispatchedState(1),
                SyncExecutionExhaustedState::class to SyncExecutionExhaustedState(1, dataLoaderRegistry)
            ))
            .build()

        val result = runBlocking {
            graphQL.executeAsync(input).await().toSpecification()
        }

        println("result: $result")
    }
}
