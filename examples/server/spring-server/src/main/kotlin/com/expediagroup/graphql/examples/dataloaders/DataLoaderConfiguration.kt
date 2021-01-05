package com.expediagroup.graphql.examples.dataloaders

import com.expediagroup.graphql.examples.model.Company
import org.dataloader.DataLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.CompletableFuture

@Configuration
class DataLoaderConfiguration {

    @Bean
    fun companyDataLoader(service: CompanyService) = DataLoader<Int, Company> { ids ->
        CompletableFuture.supplyAsync { service.getCompanies(ids) }
    }

    @Bean
    fun dataLoaderRegistryFactory(companyLoader: DataLoader<*, *>) = CustomDataLoaderRegistryFactory(companyLoader)
}
