---
id: interfaces
title: Interfaces
original_id: interfaces
---
Functions returning interfaces will automatically expose all the types implementing this interface that are available on
the classpath. Due to the GraphQL distinction between interface and a union type, interfaces need to specify at least
one common field (property or a function).

Abstract and sealed classes will also be converted to a GraphQL Interface.

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

[Abstract](https://kotlinlang.org/docs/reference/classes.html#abstract-classes) and [sealed](https://kotlinlang.org/docs/reference/sealed-classes.html) classes can also be used for interface types.

```kotlin

abstract class Shape(val area: Double)
class Circle(radius: Double) : Shape(PI * radius * radius)
class Square(sideLength: Double) : Shape(sideLength * sideLength)

sealed class Pet(val name: String) {
    class Dog(name: String, val goodBoysReceived: Int) : Pet(name)
    class Cat(name: String, val livesRemaining: Int) : Pet(name)
}

```
