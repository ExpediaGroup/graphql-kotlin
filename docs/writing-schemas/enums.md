---
id: enums
title: Enums
---

Enums are automatically mapped to GraphQL enum type.

```kotlin
enum class MyEnumType {
  @GraphQLDescription("The value to use when you only want 1 item")
  ONE,
  TWO
}
```

Above enum will be generated as following GraphQL object

```graphql
enum MyEnumType {
  """The value to use when you only want 1 item""""
  ONE
  TWO
}
```
