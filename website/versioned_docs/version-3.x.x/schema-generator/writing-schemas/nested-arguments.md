---
id: nested-arguments
title: Nested Resolvers and Shared Arguments
original_id: nested-arguments
---
There are a few ways in which shared arguments can be accessed from different resolver levels. Say we have the following schema:

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

In Kotlin code, when we are resolving  `photos`, if we want access to the parent field `findUser` and its arguments there
are a couple ways we can access it:

## DataFetchingEnvironment

You can add the `DataFetchingEnvironment` as an argument. This class will be ignored by the schema generator and will allow
you to view the entire query sent to the server. See more in the [DataFetchingEnvironment documentation](../execution/data-fetching-environment)

```kotlin

class User {
    fun photos(environment: DataFetchingEnvironment, numberOfPhotos: Int): List<Photo> {
      val username = environment.executionStepInfo.parent.arguments["id"]
      return getPhotosFromDataSource(username, numberOfPhotos)
    }
}

```

## GraphQL Context

You can add the `GraphQLContext` as an argument. This class will be ignored by the schema generator and will allow you to
view the context object you set up in the data fetchers. See more in the [GraphQLContext documentation](../execution/contextual-data)

```kotlin

class User {
    fun photos(context: MyContextObject, numberOfPhotos: Int): List<Photo> {
      val userId = context.getDataFromMyCustomFunction()
      return getPhotosFromDataSource(userId, numberOfPhotos)
    }
}

```

## Excluding Arguments from the Schema

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

## Spring Integration

See [Writing Schemas with Spring](../../spring-server/spring-schema.md) for integration details.
