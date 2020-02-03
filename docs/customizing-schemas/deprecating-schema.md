---
id: deprecating-schema
title: Deprecating Schema
---

### Deprecating Fields

GraphQL schemas can have fields marked as deprecated. Instead of creating a custom annotation,
`graphql-kotlin-schema-generator` just looks for the `kotlin.Deprecated` annotation and will use that annotation message
for the deprecated reason.

```kotlin
class SimpleQuery {
  @Deprecated(message = "this query is deprecated", replaceWith = ReplaceWith("shinyNewQuery"))
  @GraphQLDescription("old query that should not be used always returns false")
  fun simpleDeprecatedQuery(): Boolean = false

  @GraphQLDescription("new query that always returns true")
  fun shinyNewQuery(): Boolean = true
}
```

The above query would produce the following GraphQL schema:

```graphql
schema {
  query: Query
}

type Query {

  """old query that should not be used always returns false"""
  simpleDeprecatedQuery: Boolean! @deprecated(reason: "this query is deprecated, replace with shinyNewQuery")

  """new query that always returns true"""
  shinyNewQuery: Boolean!
}
```

While you can deprecate any fields/functions/classes in your Kotlin code, GraphQL only supports deprecation directive on
the fields (which correspond to Kotlin fields and functions) and enum values.

