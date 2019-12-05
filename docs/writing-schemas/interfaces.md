---
id: interfaces
title: Interfaces
---

Functions returning interfaces will automatically expose all the types implementing this interface that are available on
the classpath. Due to the GraphQL distinction between interface and a union type, interfaces need to specify at least
one common field (property or a function).

Abstract classes will also be converted to a GraphQL Interface.

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

Code above will produce the following GraphQL schema

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

### Known Issues
> NOTE: Due to a feature added in 1.0.0, we no longer support multiple levels of interfaces in a schema because the GraphQL spec does not support this feature.  [See 419](https://github.com/ExpediaGroup/graphql-kotlin/issues/419). If you do have multiple interfaces you will have to either combine them into a single interface or ignore all the parent interfaces.

#### Invalid Schema
```kotlin
interface FirstLevel {
    val id: String
}

interface SecondLevel : FirstLevel {
    val name: String
}

class MyClass(override val id: String, override val name: String) : SecondLevel
```

#### Valid Schema
```kotlin
@GraphQLIgnore
interface FirstLevel {
    val id: String
}

interface SecondLevel : FirstLevel {
    val name: String
}

class MyClass(override val id: String, override val name: String) : SecondLevel
```
OR
```kotlin
interface FirstLevel {
     val id: String
     val name: String
}

class MyClass(override val id: String, override val name: String) : FirstLevel
```
