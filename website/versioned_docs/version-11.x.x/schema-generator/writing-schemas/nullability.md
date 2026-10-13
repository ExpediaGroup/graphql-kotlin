---
id: nullability
title: Nullability
---
Both GraphQL and Kotlin have a concept of `nullable` as a marked typed. As a result we can automatically generate null
safe schemas from Kotlin code.

```kotlin
class SimpleQuery {

    fun generateNullableNumber(): Int? {
        val num = Random().nextInt(100)
        return if (num < 50) num else null
    }

    fun generateNumber(): Int = Random().nextInt(100)
}
```

The above Kotlin code would produce the following GraphQL schema:

```graphql
type Query {
  generateNullableNumber: Int

  generateNumber: Int!
}
```
