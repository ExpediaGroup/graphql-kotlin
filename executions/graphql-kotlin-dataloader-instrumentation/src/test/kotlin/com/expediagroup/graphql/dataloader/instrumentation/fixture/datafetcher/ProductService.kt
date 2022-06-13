package com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Product
import com.expediagroup.graphql.dataloader.instrumentation.fixture.extensions.toListOfNullables
import com.expediagroup.graphql.dataloader.instrumentation.fixture.repository.ProductRepository
import graphql.schema.DataFetchingEnvironment
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderOptions
import org.dataloader.stats.SimpleStatisticsCollector
import java.util.Optional
import java.util.concurrent.CompletableFuture

class ProductDataLoader : KotlinDataLoader<ProductServiceRequest, Product?> {
    override val dataLoaderName: String = "ProductDataLoader"
    override fun getDataLoader(): DataLoader<ProductServiceRequest, Product?> = DataLoaderFactory.newDataLoader(
        { requests ->
            ProductRepository
                .getProducts(requests)
                .collectList()
                .map(List<Optional<Product>>::toListOfNullables)
                .toFuture()
        },
        DataLoaderOptions.newOptions().setStatisticsCollector(::SimpleStatisticsCollector)
    )
}

data class ProductServiceRequest(val id: Int, val fields: List<String>)

class ProductService {
    fun getProduct(
        request: ProductServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<Product> =
        environment
            .getDataLoader<ProductServiceRequest, Product>("ProductDataLoader")
            .load(request)
}
