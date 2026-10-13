---
id: excluding-fields
title: Excluding Fields
---
There are two ways to ensure the GraphQL schema generation omits fields when using Kotlin reflection:

-   The first is by marking the field as non-`public` scope (`private`, `protected`, `internal`)
-   The second method is by annotating the field with `@GraphQLIgnore`.

```kotlin
class SimpleQuery {
  @GraphQLIgnore
  fun notPartOfSchema() = "ignore me!"

  private fun privateFunctionsAreNotVisible() = "ignored private function"

  fun doSomething(value: Int): Boolean = true
}
```

The above query would produce the following GraphQL schema:

```graphql
type Query {
  doSomething(value: Int!): Boolean!
}
```

Note that the public method `notPartOfSchema` is not included in the schema.
