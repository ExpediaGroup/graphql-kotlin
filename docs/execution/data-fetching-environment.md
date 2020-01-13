---
id: data-fetching-environment
title: Data Fetching Environment
---
Each resolver has access to a `DataFetchingEnvironment` that provides additional information about the currently executed query including information about what data is requested
as well as details about current execution state. For more details on the `DataFetchingEnvironment` please refer to [graphql-java documentation](https://www.graphql-java.com/documentation/v13/data-fetching/)

You can access this info by including the `DataFetchingEnvironment` as one of the arguments to a Kotlin function. This argument will be automatically populated and injected
during the query execution but will not be included in the schema definition.

```kotlin
class Query {
    fun printEnvironmentInfo(environment: DataFetchingEnvironment): String {
        // access env data
    }
}
```

This will produce the following schema

```graphql
type Query {
  printEnvironmentInfo: String!
}
```

You can also use this to retrieve arguments and query information from higher up the query chain. You can see a working
example in the `graphql-kotlin-spring-example` module
[[link](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/EnvironmentQuery.kt)].


