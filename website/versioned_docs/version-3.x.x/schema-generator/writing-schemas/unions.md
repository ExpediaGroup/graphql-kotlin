---
id: unions
title: Unions
original_id: unions
---
Marker interfaces (i.e. interfaces without any common fields or methods) are exposed as GraphQL union types. All the
types that are implementing this marker interface and are available on the classpath will be automatically exposed as
objects in the target schema.

```kotlin

interface BodyPart

data class LeftHand(val field: String): BodyPart

data class RightHand(val property: Int): BodyPart

class PolymorphicQuery {
    fun whichHand(whichHand: String): BodyPart = when (whichHand) {
        "right" -> RightHand(12)
        else -> LeftHand("hello world")
    }
}

```

The above will generate following GraphQL schema

```graphql

union BodyPart = LeftHand | RightHand

type LeftHand {
  field: String!
}

type RightHand {
  property: Int!
}

type Query {
  whichHand(whichHand: String!): BodyPart!
}

```
