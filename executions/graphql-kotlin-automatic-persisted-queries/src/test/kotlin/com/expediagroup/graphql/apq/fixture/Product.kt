package com.expediagroup.graphql.apq.fixture

data class Product(
    val id: Int,
    val summary: ProductSummary,
    val details: ProductDetails
)

data class ProductSummary(val name: String)
data class ProductDetails(val rating: String)
