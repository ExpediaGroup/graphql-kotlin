---
id: unions
title: Unions
original_id: unions
---
Marker interfaces (i.e. interfaces without any common fields or methods) are exposed as GraphQL union types. All the
types that implement the marker interface, and are available on the classpath, will be automatically exposed as
objects in the schema.

&gt; NOTE: [The GraphQL spec](http://spec.graphql.org/June2018/#sec-Unions) does not allow unions to be used as input.
&gt; This means that while it is valid Kotlin code to have a marker inteface as an argument, upon schema generation, an exception will be thrown.

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
