---
id: lists
title: Lists
---
`kotlin.collections.List` is automatically mapped to the GraphQL `List` type. Type arguments provided to Kotlin collections
are used as the type arguments in the GraphQL `List` type.

```kotlin
class SimpleQuery {
    fun generateList(): List<String> {
        // some logic here that generates list
    }

    fun doSomethingWithIntList(ints: List<Int>): String {
        // some logic here that processes list
    }
}
```

The above Kotlin class would produce the following GraphQL schema:

```graphql
type Query {
    generateList: [String!]!
    doSomethingWithIntList(ints: [Int!]!): String!
}
```

## Arrays and Unsupported Collection Types

Currently, the GraphQL spec only supports `Lists`. Therefore, even though Java and Kotlin support number of other collection
types, `graphql-kotlin-schema-generator` only explicitly supports `Lists`. Other collection types such as `Sets` (see [#201](https://github.com/ExpediaGroup/graphql-kotlin/issues/201))
and arbitrary `Map` data structures are not supported out of the box. While we do not recommend using `Map` or `Set` in the schema,
they could be supported with the use of the schema hooks.

Due to the [argument deserialization issues](https://github.com/ExpediaGroup/graphql-kotlin/pull/1379), arrays are currently not supported

```kotlin
override fun willResolveMonad(type: KType): KType = when (type.classifier) {
    Set::class -> List::class.createType(type.arguments)
    else -> type
}
```

See [Discussion #1110](https://github.com/ExpediaGroup/graphql-kotlin/discussions/1110) for more details.
