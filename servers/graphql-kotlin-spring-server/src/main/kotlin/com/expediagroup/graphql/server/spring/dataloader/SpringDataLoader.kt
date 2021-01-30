package com.expediagroup.graphql.server.spring.dataloader

import com.expediagroup.graphql.generator.execution.DefaultGraphQLContext
import com.expediagroup.graphql.server.execution.DataLoaderRegistryFactory
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.dataloader.DataLoaderRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

interface KeysDataLoader<K, V> {
    suspend fun getByKeys(keys: Set<K>, ctx: DefaultGraphQLContext): Map<K, V>
    fun loaderName(): String? = null
}

inline fun <Key, reified Value> DataFetchingEnvironment.load(
    key: Key,
    loaderPrefix: String = Value::class.java.simpleName
): CompletableFuture<Value> {
    val loaderName = "${loaderPrefix}DataLoader"
    val loader = this.getDataLoader<Key, Value>(loaderName)
        ?: throw IllegalArgumentException("No data loader called $loaderName was found")
    return loader.load(key, this.getContext())
}

@ConditionalOnProperty(value = ["graphql.spring.dataloader.enabled"], havingValue = "true")
@Configuration
class SpringDataLoaderConfiguration(private val dataLoaders: List<KeysDataLoader<*, *>>) {

    @Bean
    @Primary
    fun dataLoaderRegistryFactory(): DataLoaderRegistryFactory {
        // TODO should the number of threads be configurable?
        val coroutineDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
        val dl = dataLoaders.map { loader ->
            (loader.loaderName()?.let { "${it}DataLoader" }
                ?: loader::class.simpleName!!) to batchDataLoader(coroutineDispatcher, loader)
        }.toMap()

        return object : DataLoaderRegistryFactory {
            override fun generate() = DataLoaderRegistry().apply {
                dl.forEach { register(it.key, it.value) }
            }
        }
    }

    private fun <K, V> batchDataLoader(
        coroutineDispatcher: ExecutorCoroutineDispatcher,
        dataLoader: KeysDataLoader<K, V>
    ): DataLoader<K, V> {
        return DataLoader.newMappedDataLoader(
            { keys: Set<K>, env: BatchLoaderEnvironment ->
                GlobalScope.async(coroutineDispatcher) {
                    dataLoader.getByKeys(keys, env.keyContexts.entries.first().value as DefaultGraphQLContext)
                }.asCompletableFuture()
            },
            DataLoaderOptions.newOptions().setCachingEnabled(false) // TODO add a possibility to enable caching?
        )
    }
}
