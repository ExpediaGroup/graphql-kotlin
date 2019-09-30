---
id: data-fetching-environment 
title: Data Fetching Environment
---
Each resolver has a `DataFetchingEnvironment` that can be accessed from graphql-java:
https://www.graphql-java.com/documentation/v13/data-fetching/

You can access this info by including the `DataFetchingEnvironment` as one of the arguments to a Kotlin function. This
argument will not be included in the schema.

```kotlin
class Query {
    fun printEnvironmentInfo(environment: DataFetchingEnvironment, value: Int): String {
        // Access env data
    }
}
```

This will produce the following schema

```graphql
type Query {
  printEnvironmentInfo(value: Int!): String!
}
```

You can also use this to retrieve arguments and query information from higher up the query chain. You can see a working
example in the `graphql-kotlin-spring-example` module
[[link](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/sample/query/EnvironmentQuery.kt#L32)].
