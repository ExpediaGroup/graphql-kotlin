package com.expediagroup.graphql.dataloader.instrumentation.fixture.domain

data class Property(
    val id: Int,
    val summary: PropertySummary?,
    val details: PropertyDetails?
)
