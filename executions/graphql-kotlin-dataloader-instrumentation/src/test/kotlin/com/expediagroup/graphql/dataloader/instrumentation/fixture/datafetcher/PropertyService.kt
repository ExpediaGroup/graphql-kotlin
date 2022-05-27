package com.expediagroup.graphql.dataloader.instrumentation.fixture.datafetcher

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.expediagroup.graphql.dataloader.instrumentation.fixture.domain.Property
import com.expediagroup.graphql.dataloader.instrumentation.fixture.extensions.toListOfNullables
import com.expediagroup.graphql.dataloader.instrumentation.fixture.repository.PropertyRepository
import graphql.schema.DataFetchingEnvironment
import org.dataloader.BatchLoader
import java.util.Optional
import java.util.concurrent.CompletableFuture

class PropertyDataLoader : KotlinDataLoader<PropertyServiceRequest, Property?> {
    override val dataLoaderName: String = "PropertyDataLoader"
    override fun getBatchLoader(): BatchLoader<PropertyServiceRequest, Property?> =
        BatchLoader<PropertyServiceRequest, Property?> { requests ->
            PropertyRepository
                .getProperties(requests)
                .collectList()
                .map(List<Optional<Property>>::toListOfNullables)
                .toFuture()
        }
}

data class PropertyServiceRequest(val id: Int, val fields: List<String>)

class PropertyService {
    fun getProperty(
        request: PropertyServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<Property> =
        environment
            .getDataLoader<PropertyServiceRequest, Property>("PropertyDataLoader")
            .load(request)
}
