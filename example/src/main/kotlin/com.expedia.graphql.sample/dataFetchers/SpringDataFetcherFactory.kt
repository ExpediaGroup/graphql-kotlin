package com.expedia.graphql.sample.dataFetchers

import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactory
import graphql.schema.DataFetcherFactoryEnvironment
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLType
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.stereotype.Component

@Component
class SpringDataFetcherFactory: DataFetcherFactory<Any>, BeanFactoryAware {
    private lateinit var beanFactory: BeanFactory

    override fun setBeanFactory(beanFactory: BeanFactory?) {
        this.beanFactory = beanFactory!!
    }

    override fun get(environment: DataFetcherFactoryEnvironment?): DataFetcher<Any> {

        //Strip out possible `Input` and `!` suffixes added to by the SchemaGenerator
        val targetedTypeName = environment?.fieldDefinition?.type?.deepName?.removeSuffix("!")?.removeSuffix("Input")
        return beanFactory.getBean("${targetedTypeName}DataFetcher") as DataFetcher<Any>
    }
}

// keeping it internal
internal val GraphQLType.deepName: String
    get() = when {
        this is GraphQLNonNull -> "${this.wrappedType.deepName}!"
        this is GraphQLList -> "[${this.wrappedType.deepName}]"
        else -> name
    }