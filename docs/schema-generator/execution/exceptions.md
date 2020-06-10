---
id: exceptions
title: Exceptions and Partial Data
---

## Returning GraphQL Errors

Exceptions thrown during execution of a query will result in a GraphQLError that is added to a list of errors of the result. See
[graphql-java documentation](https://www.graphql-java.com/documentation/v14/execution/) for more details on how to customize your exception handling.


```kotlin
fun getRandomNumberOrError(): Int {
    val num = Random().nextInt(100)
    return if (num <= 50) num else throw Exception("number is greater than 50")
}
```

## Returning Data and Errors

GraphQL allows you to return both data and errors in a single response, as long as the data returned still matches the schema. Depending on the criticality of the encountered error you may want to return
null for some fields with the corresponding errors. In Kotlin, functions return only a single value, which means that in order to return both data
and errors you have to explicitly return them wrapped in a `DataFetcherResult` object.

```kotlin
class DataAndErrorsQuery {
  fun returnDataAndErrors(): DataFetcherResult<String?> {
    return DataFetcherResult.newResult<String?>()
      .data("This may return a string or null and an error")
      .error(myError)
      .build()
  }
}
```

An example of a query returning partial data is available in our [spring example app](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/DataAndErrorsQuery.kt).
