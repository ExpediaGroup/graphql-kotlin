package com.expediagroup.graphql.dataloader.instrumentation.extensions

import graphql.GraphQLContext
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.dataloader.DataLoaderFactory
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

class BatchLoaderEnvironmentExtensions {
    @Test
    fun `BatchLoaderEnvironment should access to the DataFetchingEnvironment`() {
        val stringMapperDataLoader = DataLoaderFactory.newDataLoader<String, String> { keys, batchLoaderEnvironment ->
            CompletableFuture.completedFuture(
                keys.map { key ->
                    batchLoaderEnvironment.getGraphQLContext()?.get<(String) -> String>("StringModifierLambda")?.invoke(key)
                }
            )
        }

        val stringModifierLambda = spyk(
            { string: String -> string.uppercase() }
        )

        val dataFetchingEnvironment = mockk<DataFetchingEnvironment> {
            every { graphQlContext } returns GraphQLContext.newContext().put(
                "StringModifierLambda",
                stringModifierLambda
            ).build()
        }

        stringMapperDataLoader.load("hello", dataFetchingEnvironment)
        stringMapperDataLoader.load("world", dataFetchingEnvironment)

        stringMapperDataLoader.dispatch()

        verify(exactly = 1) {
            stringModifierLambda("hello")
            stringModifierLambda("world")
        }
    }
}
