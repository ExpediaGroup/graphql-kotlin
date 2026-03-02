---
id: data-fetching-environment
title: Data Fetching Environment
---
Each resolver has access to a `DataFetchingEnvironment` that provides additional information about the currently executed query including information about what data is requested
as well as details about current execution state. For more details on the `DataFetchingEnvironment` please refer to [graphql-java documentation](https://www.graphql-java.com/documentation/data-fetching/)

You can access this info by including the `DataFetchingEnvironment` as one of the arguments to a Kotlin function. This argument will be automatically populated and injected
during the query execution but will not be included in the schema definition.

```kotlin
class Query {
    fun printEnvironmentInfo(parentField: String): MyObject = MyObject()
}

class MyObject {
  fun printParentField(childField: String, environment: DataFetchingEnvironment): String {
    val parentField = environment.executionStepInfo.parent.getArgument("parentField")
    return "The parentField was $parentField and the childField was $childField"
  }
}
```

This will produce the following schema

```graphql
type Query {
  printEnvironmentInfo(parentField: String!): MyObject!
}

type MyObject {
  printParentField(childField: String!): String!
}
```

Then the following query would return `"The parentField was foo and the childField was bar"`

```graphql
{
  printEnvironmentInfo(parentField: "foo") {
    printParentField(childField: "bar")
  }
}
```

You can also use this to retrieve arguments and query information from higher up the query chain. You can see a working
example in the `graphql-kotlin-spring-example` module [[link](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/server/spring-server/src/main/kotlin/com/expediagroup/graphql/examples/server/spring/query/EnvironmentQuery.kt)].

```kotlin
class ProductQueryService : Query {

  fun products(environment: DataFetchingEnvironment): Product {
      environment.selectionSet.fields.forEach { println("field: ${it.name}") }

    return Product(1, "Product title", 100)
  }
}

```

```graphql
{
  product {
    id
    title
    price
  }
}
```

You can also use `selectionSet` to access the selected fields of the current field. It can be useful to know which selections have been requested so the data fetcher can optimize the data access queries. For example, in an SQL-backed system, the data fetcher can access the database and use the field selection criteria to specifically retrieve only the columns that have been requested by the client.
what selection has been asked for so the data fetcher can optimise the data access queries.
For example an SQL backed system may be able to use the field selection to only retrieve the columns that have been asked for.
