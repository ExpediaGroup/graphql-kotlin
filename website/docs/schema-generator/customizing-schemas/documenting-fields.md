---
id: documenting-fields
title: Documenting Schema
---
Since Javadocs are not available at runtime for introspection, `graphql-kotlin-schema-generator` includes an annotation
class `@GraphQLDescription` that can be used to add schema descriptions to _any_ GraphQL schema element. The string value can be in the Markdown format.

```kotlin
@GraphQLDescription("A useful widget")
data class Widget(
  @GraphQLDescription("The widget's value that can be `null`")
  val value: Int?
)

class WidgetQuery {
  @GraphQLDescription("Creates new widget for given ID")
  fun widgetById(@GraphQLDescription("The special ingredient") id: Int): Widget? = Widget(id)
}
```

The above query would produce the following GraphQL schema:

```graphql
schema {
  query: Query
}

type Query {
  """Creates new widget for given ID"""
  widgetById(
    """The special ingredient"""
    id: Int!
  ): Widget
}

"""A useful widget"""
type Widget {
  """The widget's value that can be `null`"""
  value: Int
}
```
