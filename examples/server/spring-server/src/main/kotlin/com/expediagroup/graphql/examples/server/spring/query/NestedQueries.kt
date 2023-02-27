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

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.server.operations.Query
import com.fasterxml.jackson.annotation.JsonIgnore
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
class NestedQueries(private val coffeeBean: CoffeeBean) : Query {
    fun findAnimal(): NestedAnimal = NestedAnimal(1, "cat")

    @GraphQLDescription("An example of using data fetcher with out a bean factory")
    fun getCoffeeBean(beanName: String) = coffeeBean
}

data class NestedAnimal(
    val id: Int,
    @GraphQLName("animalType")
    val type: String
) {
    @JsonIgnore
    lateinit var details: NestedAnimalDetails
}

@Component
@Scope("prototype")
data class NestedAnimalDetails @Autowired(required = false) constructor(private val animalId: Int) {
    fun veryDetailedFunction(): String = "Details($animalId)"
}

@Component("NestedAnimalDetailsDataFetcher")
@Scope("prototype")
class AnimalDetailsDataFetcher : DataFetcher<NestedAnimalDetails>, BeanFactoryAware {

    private lateinit var beanFactory: BeanFactory

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    @Throws(Exception::class)
    override fun get(environment: DataFetchingEnvironment?): NestedAnimalDetails {
        val id = environment?.getSource<NestedAnimal>()?.id
        if (id == null) {
            throw Exception("Cannot retrieve animal details, the id is null")
        } else {
            return beanFactory.getBean(id)
        }
    }
}

@Component
class CoffeeBean {

    fun icedCoffee(environment: DataFetchingEnvironment, size: String): String {
        val beanChoice = environment.executionStepInfo.parent.arguments["beanName"]
        return "Iced Coffee, Bean choice: $beanChoice, size: $size"
    }

    fun hotCoffee(environment: DataFetchingEnvironment, size: String): String {
        val beanChoice = environment.executionStepInfo.parent.arguments["beanName"]
        return "Hot Coffee, Bean choice: $beanChoice, size: $size"
    }
}
