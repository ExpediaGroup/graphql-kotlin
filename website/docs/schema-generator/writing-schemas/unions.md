---
id: unions
title: Unions
---

GraphQL Kotlin allows for two ways of defining unions in the schema

## Marker Interfaces

Marker interfaces (i.e. interfaces without any common fields or methods) are exposed as GraphQL union types. All the
types that implement the marker interface, and are available on the classpath, will be automatically exposed as
objects in the schema.

:::note
[The GraphQL spec](http://spec.graphql.org/June2018/#sec-Unions) does not allow unions to be used as input.
This means that while it is valid Kotlin code to have a marker inteface as an argument, upon schema generation, an exception will be thrown.
:::

```kotlin
interface BodyPart

class LeftHand(val field: String): BodyPart

class RightHand(val property: Int): BodyPart

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

## `@GraphQLUnion`
:::note
Instead of this custom annotation, the [@GraphQLType](../customizing-schemas/custom-type-reference) annotation may be a better option
:::

The downside to marker interface unions is that you can not edit classes included in dependencies to implement new schema unions.
For example in an SDL-First world you could have this Kotlin class defined in some library.

```kotlin
class SharedModel(val foo: String)
```

And then write your schema as the following


```graphql
# From library
type SharedModel {
  foo: String!
}

# Defined in our schema
type ServiceModel {
  bar: String!
}

# Defined in our schema
union CustomUnion = SharedModel | ServiceModel

type Query {
    getModel: CustomUnion
}
```

But this is not currently possible in the full code-generation approach. Instead, you will need to use the `@GraphQLUnion` annotation on your functions or properties.

### Example Usage
```kotlin
// Defined in some other library
class SharedModel(val foo: String)

// Our code
class ServiceModel(val bar: String)

class Query {
    @GraphQLUnion(
        name = "CustomUnion",
        possibleTypes = [SharedModel::class, ServiceModel::class],
        description = "Return one or the other model"
    )
    fun getModel(): Any = ServiceModel("abc")
}
```

The annotation requires the `name` of the new union to create and the `possibleTypes` that this union can return.
However since we can not enforce the type checks anymore, you must use `Any` as the return type.

### Limitations
Since this union is defined with an added annotation it is not currently possible to add directives directly to this union definition.
You will have to modify the type with [schema generator hooks](../customizing-schemas/generator-config.md).

This limitations can be met with the [@GraphQLType](../customizing-schemas/custom-type-reference) annotation.
