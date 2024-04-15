---
id: renaming-input-types
title: Renaming Input Types
---
By default, the schema generator will use the simple name of the underlying input class for the type names. In previous versions of the library, the generator used to append `Input` suffix to the class name.
You can change this default behavior by annotating the target class with `@GraphQLInputName`. The following Kotlin `Widget` class
will be renamed to `LocalizedString` GraphQL `Input` type.

```kotlin
@GraphQLInputName("LocalizedString")
data class LocalizedStringInput(
    val value: Int?
)
```

```graphql
input LocalizedString {
    value: Int
}
```
