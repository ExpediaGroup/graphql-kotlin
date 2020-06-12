---
id: version-3.1.1-nested-arguments
title: Nested Arguments
original_id: nested-arguments
---

There are a few ways in which you can access data in a query from different levels of arguments. Say we have the following schema:

```graphql
type Query {
  findUser(id: String!): User
}

type User {
  photos(numberOfPhotos: Int!): [Photo!]!
}

type Photo {
  url: String!
}
```

In Kotlin code, when we are in the `photos` function, if we want access to the parent field `findUser` and its
arguments there are a couple ways we can access it:


## DataFetchingEnvironment
You can add the `DataFetchingEnvironment` as an argument. This class will be ignored by the schema generator and will allow you to view the entire query sent to the
  server. See more in the [DataFetchingEnvironment documentation](../execution/data-fetching-environment)

```kotlin
class User {
    fun photos(environment: DataFetchingEnvironment, numberOfPhotos: Int): List<Photo> {
      val username = environment.executionStepInfo.parent.arguments["id"]
      return getPhotosFromDataSource(username, numberOfPhotos)
    }
}
```

## GraphQL Context
You can add the `GraphQLContext` as an argument. This class will be ignored by the schema generator and will allow you to view the context object you set up in the
  data fetchers. See more in the [GraphQLContext documentation](../execution/contextual-data)

```kotlin
class User {
    fun photos(context: MyContextObject, numberOfPhotos: Int): List<Photo> {
      val userId = context.getDataFromMyCustomFunction()
      return getPhotosFromDataSource(userId, numberOfPhotos)
    }
}
```

## Excluding from the Schema
You can construct the child objects by passing down arguments as non-public fields or annotate the argument with [@GraphQLIgnore](../customizing-schemas/excluding-fields)

```kotlin
class User(private val userId: String) {

    fun photosProperty(numberOfPhotos: Int): List<Photo> {
      return getPhotosFromDataSource(userId, numberOfPhotos)
    }

    fun photosIgnore(@GraphQLIgnore userId: String, numberOfPhotos: Int): List<Photo> {
      return getPhotosFromDataSource(userId, numberOfPhotos)
    }
}
```

## Spring BeanFactoryAware
You can use Spring beans to wire different objects together at runtime.
There is an example of how to set this up in the example app in [TopLevelBeanFactoryQuery.kt](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/TopLevelBeanFactoryQuery.kt)

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
app](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/NestedQueries.kt).
