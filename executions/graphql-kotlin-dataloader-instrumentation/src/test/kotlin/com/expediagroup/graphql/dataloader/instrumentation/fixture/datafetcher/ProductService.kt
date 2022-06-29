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
