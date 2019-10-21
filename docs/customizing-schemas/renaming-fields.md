---
id: renaming-fields
title: Renaming Fields
---

By default, schema generator will use simple name of the underlying class for the type names and function/property names for field names.
You can change this default behavior by annotating target class/field with `@GraphQLName` annotation. The following Kotlin `Widget` class 
will be renamed to `MyCustomName` GraphQL type.

```kotlin
@GraphQLName("MyCustomName")
data class Widget(val value: Int?)
```

```graphql
type MyCustomName {
  value: Int
}
```
