---
id: spring-bean-factory
title: Spring BeanFactory
---

You may want to have schema inputs at different levels and if you are using Spring components that expect these inputs to be Autowired you will have to use [BeanFactory](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/BeanFactory.html).

There is an example of how to set this up in [TopLevelBeanFactoryQuery.kt](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/TopLevelBeanFactoryQuery.kt) however you can also use the alternate approach of adding the [DataFetchingEnvironment](../writing-schemas/nested-queries.md) to access the arguments in parent nodes.

Both methods have their pros and cons but you may find that the `DataFetchingEnvironment` approach means the Spring components are not directly tied together anymore and can be tested in isolation.

#### DataFetchingEnvironment
```kotlin
@Component
class TopLevelQuery : Query {
    fun topLevel(topLevelValue: String): SubQuery = SubQuery()
}

@Component
class SubQuery {
    fun printMessage(environment: DataFetchingEnvironment, subLevelValue: String): String {
        val topLevelValue = environment.executionStepInfo.parent.arguments["topLevelValue"]
        return "The top level value was '$topLevelValue' and the subLevelValue was '$subLevelValue'"
    }
}
```


#### BeanFactoryAware
```kotlin
@Component
class TopLevelQuery : Query, BeanFactoryAware {
    private lateinit var beanFactory: BeanFactory

    @GraphQLIgnore
    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    fun topLevel(topLevelValue: String): SubQuery = beanFactory.getBean(SubQuery::class.java, topLevelValue)
}

@Component
@Scope("prototype")
class SubQuery @Autowired(required = false) constructor(internal val topLevelValue: String) {
    fun printMessage(subLevelValue: String): String =
        "The top level value was '$topLevelValue' and the subLevelValue was '$subLevelValue'"
}
```
