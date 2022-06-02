package com.expediagroup.graphql.dataloader.instrumentation.fixture.domain

data class Product(
    val id: Int,
    val summary: ProductSummary?,
    val details: ProductDetails?
)
