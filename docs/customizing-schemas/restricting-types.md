---
id: restricting-types
title: Restricting Classes to GraphQL Types
---

Since we generate the types from code, your Kotlin code may have some generic class that can be used as a function argument or as a return type. However you may want to enforce that you can only use a certain class as input or output as
GraphQL does make the distinction in its type system.

With `@GraphQLTypeRestriction` you can specify what type the class should be used for and the schema generator will throw an exception if it is used incorrectly.

```kotlin
@GraphQLTypeRestriction(GraphQLType.INPUT)
data class MyQueryInput(val value: String)

// Passes generation
fun getSomeData(input: MyQueryInput): String

// Fails generation
fun getSomeData(input: String): MyQueryInput
```
