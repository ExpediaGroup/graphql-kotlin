package com.expediagroup.graphql.dataloader.instrumentation.fixture.repository

import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.ProductServiceRequest
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Product
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.ProductDetails
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.ProductSummary
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.Optional

object ProductRepository {
    private val products = listOf(
        Product(
            1,
            ProductSummary("Product 1"),
            ProductDetails("5 out of 10")
        ),
        Product(
            2,
            ProductSummary("Product 2"),
            ProductDetails("10 out of 10")
        )
    )

    /**
     * let's assume data batch loader provides 4 requests "keys" to getProducts
     * - 2 for productId 1 fetching summary and details respectively
     * - 2 for productId 2 fetching summary and details respectively
     *
     *  here we would need to aggregate 2 requests for each productId into 1 like this
     *  - 1 request for productId 1 fetching summary and details
     *  - 1 request for productId 1 fetching summary and details
     */
    fun getProducts(requests: List<ProductServiceRequest>): Flux<Optional<Product>> {
        val reducedRequests = requests
            .groupBy(ProductServiceRequest::id)
            .mapValues { (productId, requests) ->
                ProductServiceRequest(
                    productId,
                    requests.map(ProductServiceRequest::fields).flatten().distinct()
                )
            }.values.toList()

        val results = reducedRequests.mapNotNull { productRequest ->
            products
                .firstOrNull { it.id == productRequest.id }
                ?.let { property ->
                    Product(
                        property.id,
                        when {
                            productRequest.fields.contains("summary") -> property.summary
                            else -> null
                        },
                        when {
                            productRequest.fields.contains("details") -> property.details
                            else -> null
                        }
                    )
                }
        }.associateBy(Product::id)

        return requests
            .toFlux()
            .flatMap { request ->
                Optional.ofNullable(results[request.id]).toMono().delayElement(Duration.ofMillis(200))
            }
    }
}
