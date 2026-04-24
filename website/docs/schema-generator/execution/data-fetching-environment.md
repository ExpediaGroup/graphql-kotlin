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

## Coercing Arguments to Typed Objects

`environment.arguments` returns a `Map<String, Any?>`. By the time your code sees this map, graphql-java has already run all custom scalar coercers, so scalar fields are already in their target JVM types — not raw strings.

These utilities use `KClass.primaryConstructor` (Kotlin-side reflection), which means they:
- correctly pass through field values that graphql-java has already coerced (custom scalars, etc.)
- resolve field names using `@GraphQLName` or the Kotlin parameter name — the same logic used to build the schema
- respect Kotlin default parameter values for fields absent from the map

This is the same coercion path that `FunctionDataFetcher` uses internally for resolver parameters.

`ObjectMapper.convertValue` is not a suitable alternative: it resolves field names via Jackson annotations or naming strategies rather than `@GraphQLName`, and it will attempt to re-deserialize values that graphql-java has already coerced.

### Coercing the full arguments map

Use `getArgumentsAs` from `com.expediagroup.graphql.generator.extensions` when you want to coerce all arguments on a field at once. The target class constructor parameters must correspond directly to the GraphQL argument names on the field.

```kotlin
import com.expediagroup.graphql.generator.extensions.getArgumentsAs
```

```graphql
type Query {
  search(query: String!, limit: Int!): [Result!]!
}
```

```kotlin
data class SearchArgs(val query: String, val limit: Int)

val args = environment.getArgumentsAs<SearchArgs>()
```

### Coercing a single argument value

In instrumentation or library code where you dynamically inspect a field's arguments, you often need to coerce a specific argument's value rather than the full arguments map. Use `convertInputMap` from `com.expediagroup.graphql.generator.execution` for this:

```kotlin
import com.expediagroup.graphql.generator.execution.convertInputMap
```

```kotlin
// In an Instrumentation.beginFieldFetch implementation:
val targetArgument = parameters.environment.fieldDefinition.arguments
    .find { it.type.deepName == "MyInput!" }

targetArgument?.let {
    val rawMap = parameters.environment.arguments[it.name] as? Map<String, *>
    if (rawMap != null) {
        val input = convertInputMap(rawMap, MyInput::class)
        // use input ...
    }
}
```
