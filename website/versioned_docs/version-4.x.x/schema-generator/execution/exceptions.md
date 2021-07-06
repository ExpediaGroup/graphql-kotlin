---
id: exceptions
title: Exceptions and Partial Data
---
## Returning GraphQL Errors

Exceptions thrown during execution of an operation will result in an empty data response and a GraphQLError that is added to a list of errors of the result.
See [graphql-java documentation](https://www.graphql-java.com/documentation/v14/execution/) for more details on how to customize your exception handling.

```kotlin
fun getRandomNumberOrError(): Int {
    val num = Random().nextInt(100)
    return if (num <= 50) num else throw Exception("number is greater than 50")
}
```

## Returning Data and Errors

GraphQL allows you to return both data and errors in a single response, as long as the data returned still matches the schema. Depending on the criticality of the encountered error, instead of throwing an exception, you may want to return
default data or use a nullable field, but still include more information in the `errors` block. In Kotlin, functions return only a single value, which means that in order to return both data
and errors you have to explicitly return them wrapped in a `DataFetcherResult` object.

```kotlin
class DataAndErrorsQuery {
  fun returnDataAndErrors(): DataFetcherResult<String?> {
    val data: String? = getData()
    val error = if (data == null) MyError() else null

    return DataFetcherResult.newResult<String?>()
      .data(data)
      .error(error)
      .build()
  }
}
```

An example of a query returning partial data is available in our [spring example app](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/DataAndErrorsQuery.kt).
