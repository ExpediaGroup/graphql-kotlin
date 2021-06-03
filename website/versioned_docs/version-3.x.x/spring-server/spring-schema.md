---
id: spring-schema
title: Writing Schemas with Spring
original_id: spring-schema
---

In order to expose your queries, mutations and/or subscriptions in the GraphQL schema you need to create beans that
implement corresponding marker interface and they will be automatically picked up by `graphql-kotlin-spring-server`
auto-configuration library.

```kotlin
@Component
class MyAwesomeQuery : Query {
  fun myAwesomeQuery(): Widget { ... }
}

@Component
class MyAwesomeMutation : Mutation {
  fun myAwesomeMutation(widget: Widget): Widget { ... }
}

@Component
class MyAwesomeSubscription : Subscription {
  fun myAwesomeSubscription(): Publisher<Widget> { ... }
}

data class Widget(val id: Int, val value: String)
```

will result in a Spring Boot reactive GraphQL web application with following schema.

```graphql
schema {
  query: Query
  mutation: Mutation
  subscription: Subscription
}

type Query {
  myAwesomeQuery: Widget!
}

type Mutation {
  myAwesomeMutation(widget: WidgetInput!): Widget!
}

type Subscription {
  myAwesomeSubscription: Widget!
}

type Widget {
  id: Int!
  value: String!
}

input WidgetInput {
  id: Int!
  value: String!
}
```

## Spring Query Beans

Spring will automatically autowire dependent beans to our Spring query beans. Refer to [Spring Documentation](https://docs.spring.io/spring/docs/current/spring-framework-reference/) for details.

```kotlin
@Component
class WidgetQuery(private val repository: WidgetRepository) : Query {
    fun getWidget(id: Int): Widget = repository.findWidget(id)
}
```

## Spring Data Fetcher

`graphql-kotlin-spring-server` provides Spring aware data fetcher that automatically autowires Spring beans when they are
specified as function arguments. `@Autowired` arguments should be explicitly excluded from the GraphQL schema by also
specifying `@GraphQLIgnore`.

```kotlin
@Component
class SpringQuery : Query {
    fun getWidget(@GraphQLIgnore @Autowired repository: WidgetRepository, id: Int): Widget = repository.findWidget(id)
}
```

> NOTE: if you are using custom data fetcher make sure that you extend `SpringDataFetcher` instead of a base `FunctionDataFetcher`.

## Spring BeanFactoryAware

You can use Spring beans to wire different objects together at runtime. Instead of autowiring specific beans as properties,
you can also dynamically resolve beans by using bean factories. There is an example of how to set this up in the example
app in the [TopLevelBeanFactoryQuery.kt](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/TopLevelBeanFactoryQuery.kt).

```kotlin
@Component
class UsersQuery : Query, BeanFactoryAware {
    private lateinit var beanFactory: BeanFactory

    @GraphQLIgnore
    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    fun findUser(id: String): SubQuery = beanFactory.getBean(User::class.java, id)
}

@Component
@Scope("prototype")
class User @Autowired(required = false) constructor(private val userId: String) {

    @Autowired
    private lateinit var service: PhotoService

    fun photos(numberOfPhotos: Int): List<Photo> = service.findPhotos(userId, numberOfPhotos)
}
```

------

We have examples of these techniques implemented in Spring boot in the [example
app](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/NestedQueries.kt).
