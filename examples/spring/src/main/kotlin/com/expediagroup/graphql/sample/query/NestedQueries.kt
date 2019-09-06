package com.expediagroup.graphql.sample.query

import com.expediagroup.graphql.annotations.GraphQLDescription
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

    @GraphQLDescription("An exmaple of using data fetcher with out a bean factory")
    fun getCoffeeBean(beanName: String) = coffeeBean
}

data class NestedAnimal(
        val id: Int,
        val type: String
) {
    @JsonIgnore
    lateinit var details: NestedAnimalDetails
}

@Component
@Scope("prototype")
data class NestedAnimalDetails @Autowired(required = false) constructor(private val animalId: Int) {
    fun veryDetailledFunction(): String = "Details($animalId)"
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

    fun icedCofee(environment: DataFetchingEnvironment, size: String): String {
        val beanChoice = environment.executionStepInfo.parent.arguments["beanName"]
        return "Iced Coffee, Bean choice: $beanChoice, size: $size"
    }

    fun hotCofee(environment: DataFetchingEnvironment, size: String): String {
        val beanChoice = environment.executionStepInfo.parent.arguments["beanName"]
        return "Hot Coffee, Bean choice: $beanChoice, size: $size"
    }
}
