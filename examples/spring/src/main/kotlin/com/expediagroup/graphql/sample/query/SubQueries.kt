package com.expediagroup.graphql.sample.query

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLIgnore
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.getBean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
class SubQueries: Query, BeanFactoryAware {
    private lateinit var beanFactory: BeanFactory

    @GraphQLIgnore
    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    @GraphQLDescription(
        """Creates a hierarchy of executable fields.
        Arguments can be passed from `main` to the `SubQuery` via the constructor invoke by the bean factory"""
    )
    fun main(): SubQuery = beanFactory.getBean()
}

@Component
@Scope("prototype")
class SubQuery {
    fun secondary() = "secondary"
}
