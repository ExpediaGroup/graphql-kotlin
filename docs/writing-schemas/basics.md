---
id: basics 
title: Basics
---
`graphql-kotlin-schema-generator` provides a single function, `toSchema`, to generate a schema from Kotlin objects. This
function accepts four arguments: `config`, `queries`, `mutations` and `subscriptions`. The `queries`, `mutations` and
`subscriptions` are a list of `TopLevelObject`s and will be used to generate corresponding GraphQL root types. See below
on why we use this wrapper class. The `config` contains all the extra information you need to pass, including custom
hooks, supported packages, and name overrides.
See the [Generator Configuration](generator-config) documentation for more information.

A query/mutation/subscription type is simply a Kotlin class that specifies **fields**, which can be functions or
properties:

```kotlin
data class Widget(val id: Int, val value: String)

class WidgetQuery {
  fun widgetById(id: Int): Widget? {
    // grabs widget from a data source
  }
}

class WidgetMutation {
  fun saveWidget(value: String): Widget {
    // some logic goes here
  }
}

val widgetQuery = WidgetQuery()
val widgetMutation = WidgetMutation()
val schema = toSchema(
  config = yourCustomConfig()
  queries = listOf(TopLevelObject(widgetQuery)),
  mutations = listOf(TopLevelObject(widgetMutation))
)
```

will generate:

```graphql
schema {
  query: Query
  mutation: Mutation
}

type Query {
  widgetById(id: Int!): Widget
}

type Mutation {
  saveWidget(value: String!): Widget!
}

type Widget {
  id: Int!
  value: String!
}
```

Any `public` functions defined on a query or mutation Kotlin class will be translated into GraphQL fields on the object
type. `toSchema` will then recursively apply Kotlin reflection on the specified queries and mutations to generate all
remaining object types, their properties, functions, and function arguments.
