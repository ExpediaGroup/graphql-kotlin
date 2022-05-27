package com.expediagroup.graphql.dataloader.instrumentation.fixture.repository

import com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher.PropertyServiceRequest
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Property
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.PropertyDetails
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.PropertySummary
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.Optional

object PropertyRepository {
    private val properties = listOf(
        Property(
            1,
            PropertySummary("Property 1"),
            PropertyDetails("5 out of 10")
        ),
        Property(
            2,
            PropertySummary("Property 2"),
            PropertyDetails("10 out of 10")
        )
    )

    fun getProperties(requests: List<PropertyServiceRequest>): Flux<Optional<Property>> {
        // property service request: 1, summary
        // property service request: 1, details

        // property service request: 2, summary
        // property service request: 2, details

        // ==========

        // property service request: 1, summary|details
        // property service request: 2, summary|details
        val reducedRequests = requests
            .groupBy(PropertyServiceRequest::id)
            .mapValues { (propertyId, requests) ->
                PropertyServiceRequest(
                    propertyId,
                    requests.map(PropertyServiceRequest::fields).flatten().distinct()
                )
            }.values.toList()

        val results = reducedRequests.mapNotNull { propertyRequest ->
            properties
                .firstOrNull { it.id == propertyRequest.id }
                ?.let { property ->
                    Property(
                        property.id,
                        when {
                            propertyRequest.fields.contains("summary") -> property.summary
                            else -> null
                        },
                        when {
                            propertyRequest.fields.contains("details") -> property.details
                            else -> null
                        }
                    )
                }
        }.associateBy(Property::id)

        return requests
            .toFlux()
            .flatMap { request ->
                Optional.ofNullable(results[request.id]).toMono().delayElement(Duration.ofMillis(200))
            }
    }
}
