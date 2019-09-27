---
id: fields
title: Fields
---

Any **public** fields on the returned objects will be exposed as part of the schema unless they are explicitly marked to
be ignored with `@GraphQLIgnore` annotation. Documentation and deprecation information is also supported. For more
details about different annotations see sections below.

### Documenting Fields

Since Javadocs are not available at runtime for introspection, `graphql-kotlin-schema-generator` includes an annotation
class `@GraphQLDescription` that can be used to add schema descriptions to *any* GraphQL schema element:

```kotlin
@GraphQLDescription("A useful widget")
data class Widget(
  @GraphQLDescription("The widget's value that can be null")
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
  """The widget's value that can be null"""
  value: Int
}
```

You can also override the name used by the generator with `@GraphQLName`. The following schema would be renamed after
generation

```kotlin
@GraphQLDescription("A useful widget")
@GraphQLName("MyCustomName")
data class Widget(val value: Int?)
```

```graphql
"""A useful widget"""
type MyCustomName {
  value: Int
}
```

### Excluding Fields from Schema

There are two ways to ensure the GraphQL schema generation omits fields when using Kotlin reflection:

* The first is by marking the field as non-`public` scope (`private`, `protected`, `internal`)
* The second method is by annotating the field with `@GraphQLIgnore`.

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
schema {
  query: Query
}

type Query {
  doSomething(value: Int!): Boolean!
}
```

Note that the public method `notPartOfSchema` is not included in the schema.

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
