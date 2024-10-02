---
id: interfaces
title: Interfaces
---
Kotlin interfaces, abstract and sealed classes will be mapped to a GraphQL interface. Due to the GraphQL distinction between interface
and a [union type](./unions.md), Kotlin interfaces need to specify at least one common field (property or a function). Superclasses and
interfaces can be excluded from the schema by marking them with `@GraphQLIgnore` annotation or by providing custom filtering logic in a
custom schema generator hook.

:::note
[The GraphQL spec](http://spec.graphql.org/June2018/#sec-Interfaces) does not allow interfaces to be used as input.
This means that while it is valid Kotlin code to have an interface as an argument, upon schema generation, an exception will be thrown.
:::

```kotlin
interface Animal {
    val type: AnimalType
    fun sound(): String
}

enum class AnimalType {
    CAT,
    DOG
}

class Dog : Animal {
    override val type = AnimalType.DOG

    override fun sound() = "bark"

    fun barkAtEveryone(): String = "bark at everyone"
}

class Cat : Animal {
    override val type = AnimalType.CAT

    override fun sound() = "meow"

    fun ignoreEveryone(): String = "ignore everyone"
}

class PolymorphicQuery {

    fun animal(type: AnimalType): Animal? = when (type) {
        AnimalType.CAT -> Cat()
        AnimalType.DOG -> Dog()
        else -> null
    }
}
```

The above code will produce the following GraphQL schema:

```graphql
interface Animal {
  type: AnimalType!
  sound: String!
}

enum AnimalType {
  CAT
  DOG
}

type Cat implements Animal {
  type: AnimalType!
  ignoreEveryone: String!
  sound: String!
}

type Dog implements Animal {
  type: AnimalType!
  barkAtEveryone: String!
  sound: String!
}

type TopLevelQuery {
  animal(type: AnimalType!): Animal
}
```

## Abstract and Sealed Classes

[Abstract](https://kotlinlang.org/docs/reference/classes.html#abstract-classes) and [sealed](https://kotlinlang.org/docs/reference/sealed-classes.html) classes can also be used for GraphQL interface types.

```kotlin
abstract class Shape(val area: Double)
class Circle(radius: Double) : Shape(PI * radius * radius)
class Square(sideLength: Double) : Shape(sideLength * sideLength)

sealed class Pet(val name: String) {
    class Dog(name: String, val goodBoysReceived: Int) : Pet(name)
    class Cat(name: String, val livesRemaining: Int) : Pet(name)
}
```

## Nested Interfaces

Interfaces can implement other interfaces.

```kotlin
interface Foo {
    val foo: String
}

interface Bar : Foo {
    val bar: String
}

class Baz(override val foo: String, override val bar: String) : Bar
```

Code above generates following schema

```graphql
interface Foo {
  foo: String!
}

interface Bar implements Foo {
  bar: String!
  foo: String!
}

type Baz implements Bar & Foo {
  bar: String!
  foo: String!
}
```
