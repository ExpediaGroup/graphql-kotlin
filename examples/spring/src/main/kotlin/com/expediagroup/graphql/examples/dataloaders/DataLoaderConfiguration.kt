package com.expediagroup.graphql.examples.dataloaders

import com.expediagroup.graphql.examples.model.Company
import com.expediagroup.graphql.examples.query.CompanyService
import com.expediagroup.graphql.spring.execution.DataLoaderRegistryFactory
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.CompletableFuture

@Configuration
class DataLoaderConfiguration(private val companyService: CompanyService) {
    @Bean
    fun dataLoaderRegistryFactory(): DataLoaderRegistryFactory {
        return object : DataLoaderRegistryFactory {
            override fun generate(): DataLoaderRegistry {
                val registry = DataLoaderRegistry()
                val companyLoader = DataLoader<Int, Company> { ids ->
                    CompletableFuture.supplyAsync { companyService.getCompanies(ids) }
                }
                registry.register("companyLoader", companyLoader)
                return registry
            }
        }
    }
}
