---
id: nested-queries
title: Nested Queries
---

There are a few ways in which you can access data in a nested query. Say we have the following schema:

```graphql
type Query {
  findUsers(name: String!): User
}

type User {
  id: ID!
  photos(numberOfPhotos: Int!): [Photo!]!
}

type Photo {
  url: String!
}
```

In Kotlin code, when we are in the `photos` function, if we want access to the parent field `findUsers` and its
arguments there are a couple ways we can access it:

* You can add the `DataFetchingEnvironment` as an argument which will allow you to view the entire query sent to the
  server. See more in the [DataFetchingEnvironment documentation](../execution/data-fetching-environment)

```kotlin
fun photos(environment: DataFetchingEnvironment, numberOfPhotos: Int): List<Photo> {
  val nameInput = environment.executionStepInfo.parent.arguments["name"]
  return getPhotosFromDataSource()
}
```

* You can add the `GraphQLContext` as an argument which will allow you to view the context object you set up in the
  data fetchers. See more in the [GraphQLContext documentation](../execution/contextual-data)

```kotlin
fun photos(context: MyContextObject, numberOfPhotos: Int): List<Photo> {
  val nameInput = context.getDataFromMyCustomFunction()
  return getPhotosFromDataSource()
}
```

------

We have examples of these techniques implemented in Spring boot in the [example
app](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/NestedQueries.kt).
