---
id: exceptions
title: Exceptions and Partial Data
---

Exceptions thrown during execution of a query will result in a GraphQLError that is added to a list of errors of the result. See
[graphql-java documentation](https://www.graphql-java.com/documentation/v13/execution/) for more details on how to customize your exception handling.

### Partial Data

GraphQL allows you to return both data and errors in a single response. Depending on the criticality of the encountered error you may want to return
partial data together with the corresponding errors. In Kotlin, functions return only a single value, which means that in order to return both data
and errors you have to explicitly return them wrapped in a `DataFetcherResult` object.

```kotlin
class DataAndErrorsQuery {
  fun returnDataAndErrors(): DataFetcherResult<String> {
    // some logic here to populate data and capture error
    return DataFetcherResult.newResult<String>()
      .data(myData)
      .error(myError)
      .build()
  }
}
```

An example of a query returning partial data is available in our [spring example app](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/DataAndErrorsQuery.kt).
