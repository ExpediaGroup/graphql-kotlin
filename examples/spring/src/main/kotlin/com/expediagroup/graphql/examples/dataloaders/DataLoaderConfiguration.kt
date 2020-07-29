package com.expediagroup.graphql.examples.dataloaders

import com.expediagroup.graphql.examples.query.Company
import com.expediagroup.graphql.spring.execution.DataLoaderRegistryFactory
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
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

@Component
class CompanyService {
    private val companies = listOf(
        Company(id = 1, name = "FirstCompany"),
        Company(id = 2, name = "SecondCompany")
    )

    fun getCompanies(ids: List<Int>): List<Company> = companies
}

