package com.expediagroup.graphql.server.spring.dataloader

import com.expediagroup.graphql.generator.execution.DefaultGraphQLContext
import graphql.schema.DataFetchingEnvironment
import graphql.schema.DataFetchingEnvironmentImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals

class SpringDataLoaderTest {

    @Test
    fun `DataFetchingEnvironment throw exception when dataloader is not found`() {
        val dfe: DataFetchingEnvironment = mockk {
            every { getDataLoader<String, String>("StringDataLoader") } returns null
        }

        assertThatThrownBy {
            dfe.load<String, String>("test")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `DataFetdchingEnvironment get dataloader and load content`() {
        val dataloader = DataLoader.newDataLoader { _: List<String> ->
            CompletableFuture.supplyAsync { listOf("hello") }
        }
        val registry = DataLoaderRegistry().apply {
            register("StringDataLoader", dataloader)
        }

        val dfe = DataFetchingEnvironmentImpl
            .newDataFetchingEnvironment()
            .dataLoaderRegistry(registry)
            .build()

        val result = dfe.load<String, String>(key = "test", loaderPrefix = "String").also { dataloader.dispatch() }
        assertEquals(result.get(), "hello")
    }

    @Test
    fun `Register data loader`() {
        val dataloader = object : KeysDataLoader<String, String> {
            override suspend fun getByKeys(keys: Set<String>, ctx: DefaultGraphQLContext) = emptyMap<String, String>()
        }
        val dataloaderConfig = SpringDataLoaderConfiguration(listOf(dataloader))
        val registry = dataloaderConfig.dataLoaderRegistryFactory().generate()
        val result = runBlocking { dataloader.getByKeys(emptySet(), DefaultGraphQLContext()) }

        assertEquals(registry.dataLoaders.size, 1)
        assertEquals(result.size, 0)
    }
}
