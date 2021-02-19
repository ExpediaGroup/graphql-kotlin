---
id: client-features
title: Client Features
original_id: client-features
---
## Polymorphic Types Support

GraphQL supports polymorphic types through unions and interfaces which can be represented in Kotlin as marker and
regular interfaces. In order to ensure generated objects are not empty, GraphQL queries referencing polymorphic types
have to **explicitly specify all implementations**. Polymorphic queries also have to explicitly request `__typename`
field so it can be used to Jackson correctly distinguish between different implementations.

Given example schema

```graphql

type Query {
  interfaceQuery: BasicInterface!
}

interface BasicInterface {
  id: Int!
  name: String!
}

type FirstInterfaceImplementation implements BasicInterface {
  id: Int!
  intValue: Int!
  name: String!
}

type SecondInterfaceImplementation implements BasicInterface {
  floatValue: Float!
  id: Int!
  name: String!
}

```

We can query interface field as

```graphql

query PolymorphicQuery {
  interfaceQuery {
    __typename
    id
    name
    ... on FirstInterfaceImplementation {
      intValue
    }
    ... on SecondInterfaceImplementation {
      floatValue
    }
  }
}

```

Which will generate following data model

```kotlin

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "__typename"
)
@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    PolymorphicQuery.FirstInterfaceImplementation::class,
    name="FirstInterfaceImplementation"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value
    = PolymorphicQuery.SecondInterfaceImplementation::class, name="SecondInterfaceImplementation")])
interface BasicInterface {
  val id: Int
  val name: String
}

data class FirstInterfaceImplementation(
  override val id: Int,
  override val name: String,
  val intValue: Int
) : PolymorphicQuery.BasicInterface

data class SecondInterfaceImplementation(
  override val id: Int,
  override val name: String,
  val floatValue: Float
) : PolymorphicQuery.BasicInterface

```

## Default Enum Values

Enums represent predefined set of values. Adding additional enum values could be a potentially breaking change as your
clients may not be able to process it. GraphQL Kotlin Client automatically adds default `@JsonEnumDefaultValue __UNKNOWN_VALUE`
to all generated enums as a catch all safeguard for handling new enum values.

## Auto Generated Documentation

GraphQL Kotlin build plugins automatically pull in GraphQL descriptions of the queried fields from the target schema and
add it as KDoc to corresponding data models.

Given simple GraphQL object definition

```graphql

"Some basic description"
type BasicObject {
  "Unique identifier"
  id: Int!
  "Object name"
  name: String!
}

```

Will result in a corresponding auto generated data class

```kotlin

/**
 * Some basic description
 */
data class BasicObject(
  /**
   * Unique identifier
   */
  val id: Int,
  /**
   * Object name
   */
  val name: String
)

```

## Native Support for Coroutines

GraphQL Kotlin Client is a thin wrapper on top of [Ktor HTTP Client](https://ktor.io/clients/index.html) which provides
fully asynchronous communication through Kotlin coroutines. `GraphQLClient` exposes single `execute` method that will
suspend your GraphQL operation until it gets a response back without blocking the underlying thread. In order to fetch
data asynchronously and perform some additional computations at the same time you should wrap your client execution in
`launch` or `async` coroutine builder and explicitly `await` for their results.

See [Kotlin coroutines documentation](https://kotlinlang.org/docs/reference/coroutines-overview.html) for additional details.
