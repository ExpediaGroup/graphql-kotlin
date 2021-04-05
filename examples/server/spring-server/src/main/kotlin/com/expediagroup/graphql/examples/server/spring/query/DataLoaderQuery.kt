/*
 * Copyright 2021 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.examples.server.spring.dataloaders.CompanyDataLoader
import com.expediagroup.graphql.examples.server.spring.model.Company
import com.expediagroup.graphql.examples.server.spring.model.Employee
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import com.expediagroup.graphql.server.operations.Query
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

/**
 * Example query that showcases the usage of data loader pattern.
 */
@Component
class DataLoaderQuery : Query {
    private val employees = listOf(
        Employee(name = "Mike", companyId = 1, skills = setOf("sales", "sales")),
        Employee(name = "John", companyId = 1, skills = setOf("management")),
        Employee(name = "Steve", companyId = 2)
    )

    @GraphQLDescription("Get all employees")
    fun employees(): List<Employee> {
        return employees
    }
}

/**
 * Register a custom [DataFetcher] to get a [Company] any time it appears in the schema
 */
@Component("CompanyDataFetcher")
class CompanyDataFetcher : DataFetcher<CompletableFuture<Company>> {

    override fun get(environment: DataFetchingEnvironment): CompletableFuture<Company> {
        val companyId = environment.getSource<Employee>().companyId
        return environment.getValueFromDataLoader(CompanyDataLoader.name, companyId)
    }
}
