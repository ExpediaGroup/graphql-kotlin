package com.expediagroup.graphql.examples.query

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.spring.operations.Query
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

/**
 * Example query that showcases the usage of data loader pattern.
 */
@Component
class DataLoaderQuery : Query {
    private val employees = listOf(
        Employee(name = "Mike", companyId = 1),
        Employee(name = "John", companyId = 1),
        Employee(name = "Steve", companyId = 2)
    )

    @GraphQLDescription("Get all employees")
    fun employees(): List<Employee> {
        return employees
    }
}

data class Employee(
    val name: String,
    @GraphQLIgnore
    val companyId: Int
) {
    lateinit var company: Company
}

data class Company(val id: Int, val name: String)

@Component("CompanyDataFetcher")
@Scope("prototype")
class CompanyDataFetcher : DataFetcher<CompletableFuture<Company>>, BeanFactoryAware {
    private lateinit var beanFactory: BeanFactory

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    override fun get(environment: DataFetchingEnvironment): CompletableFuture<Company> {
        val companyId = environment.getSource<Employee>().companyId
        return environment
            .getDataLoader<Int, Company>("companyLoader")
            .load(companyId)
    }
}
