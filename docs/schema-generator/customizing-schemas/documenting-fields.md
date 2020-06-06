---
id: documenting-fields
title: Documenting Schema
---

Since Javadocs are not available at runtime for introspection, `graphql-kotlin-schema-generator` includes an annotation
class `@GraphQLDescription` that can be used to add schema descriptions to *any* GraphQL schema element. The string value can be in the Markdown format, however due to an [issue in graphql-java](https://github.com/graphql-java/graphql-java/issues/1677) the `#` character is not supported to mark header levels.

```kotlin
@GraphQLDescription("A useful widget")
data class Widget(
  @GraphQLDescription("The widget's value that can be `null`")
  val value: Int?
)

class WidgetQuery: Query {

  @GraphQLDescription("creates new widget for given ID")
  fun widgetById(@GraphQLDescription("The special ingredient") id: Int): Widget? = Widget(id)
}
```

The above query would produce the following GraphQL schema:

```graphql
schema {
  query: Query
}

type Query {
  """creates new widget for given ID"""
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
