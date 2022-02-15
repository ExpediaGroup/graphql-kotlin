---
id: deprecating-schema
title: Deprecating Schema
---
GraphQL schemas can have fields marked as deprecated. Instead of creating a custom annotation,
`graphql-kotlin-schema-generator` just looks for the `kotlin.Deprecated` annotation and will use that annotation message
for the deprecated reason.

```kotlin
class SimpleQuery {
  @Deprecated(message = "this query is deprecated", replaceWith = ReplaceWith("shinyNewQuery"))
  fun simpleDeprecatedQuery(): Boolean = false

  fun shinyNewQuery(): Boolean = true
}
```

The above query would produce the following GraphQL schema:

```graphql
type Query {
  simpleDeprecatedQuery: Boolean! @deprecated(reason: "this query is deprecated, replace with shinyNewQuery")

  shinyNewQuery: Boolean!
}
```

While you can deprecate any fields/functions/classes in your Kotlin code, GraphQL only supports deprecation directive on
the fields (which correspond to Kotlin properties and functions), input fields and enum values.

Deprecation of arguments is currently not supported [in Kotlin](https://youtrack.jetbrains.com/issue/KT-25643).
