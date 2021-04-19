---
id: lists
title: Lists
---
Both `kotlin.Array` and `kotlin.collections.List` are automatically mapped to the GraphQL `List` type (for unsupported
use cases see below). Type arguments provided to Kotlin collections are used as the type arguments in the GraphQL `List`
type. Kotlin specialized classes (e.g. `IntArray`) representing arrays of Java primitive types without boxing overhead
are also supported.

```kotlin
class SimpleQuery {
    fun generateList(): List<Int> {
        // some logic here that generates list
    }

    fun doSomethingWithIntArray(ints: IntArray): String {
        // some logic here that processes array
    }

    fun doSomethingWithIntList(ints: List<Int>): String {
        // some logic here that processes list
    }
}
```

The above Kotlin class would produce the following GraphQL schema:

```graphql
type Query {
    generateList: [Int!]!
    doSomethingWithIntArray(ints: [Int!]!): String!
    doSomethingWithIntList(ints: [Int!]!): String!
}
```

## Primitive Arrays

`graphql-kotlin-schema-generator` supports the following primitive array types without autoboxing overhead. Similarly to
the `kotlin.Array` of objects the underlying type is automatically mapped to GraphQL `List` type.

| Kotlin Type                  |
| ---------------------------- |
| `kotlin.IntArray`     |
| `kotlin.LongArray`    |
| `kotlin.ShortArray`   |
| `kotlin.FloatArray`   |
| `kotlin.DoubleArray`  |
| `kotlin.CharArray`    |
| `kotlin.BooleanArray` |

:::note
The underlying GraphQL types of primitive arrays will be corresponding to the built-in scalar types or extended scalar types provided by `graphql-java`.
:::

## Unsupported Collection Types

Currently, the GraphQL spec only supports `Lists`. Therefore, even though Java and Kotlin support number of other collection
types, `graphql-kotlin-schema-generator` only explicitly supports `Lists` and primitive arrays. Other collection types
such as `Sets` (see [#201](https://github.com/ExpediaGroup/graphql-kotlin/issues/201)) and arbitrary `Map` data
structures are not supported out of the box. While we do not reccomend using `Map` or `Set` in the schema,
they are supported with the use of the schema hooks.

```kotlin
override fun willResolveMonad(type: KType): KType = when (type.classifier) {
    Set::class -> List::class.createType(type.arguments)
    else -> type
}
```

See [Discussion #1110](https://github.com/ExpediaGroup/graphql-kotlin/discussions/1110) for more details.
