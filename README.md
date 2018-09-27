# GraphQL Kotlin

[![Build Status](https://travis-ci.org/ExpediaDotCom/graphql-kotlin.svg?branch=master)](https://travis-ci.org/ExpediaDotCom/graphql-kotlin) [![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.expedia.www/graphql-kotlin/badge.png)](https://maven-badges.herokuapp.com/maven-central/com.expedia.www/graphql-kotlin)

Most GraphQL libraries for the JVM require developers to maintain two sources of truth for their GraphQL API, the schema and the corresponding code (data fetchers and types). Given the similarities between Kotlin and GraphQL, such as the ability to define nullable/non-nullable types, a schema should be able to be generated from Kotlin code without any separate schema specification. `graphql-kotlin` builds upon `graphql-java` to allow code-only GraphQL services to be built.

For information on GraphQL, please visit [the GraphQL website](https://graphql.org/).

For information on `graphql-java`, please visit [GraphQL Java](https://graphql-java.readthedocs.io/en/latest/).

# Getting started

## Installation

Using a JVM dependency manager, simply link `graphql-kotlin` to your project.

With Maven:

```xml
<dependency>
  <groupId>com.expedia.www</groupId>
  <artifactId>graphql-kotlin</artifactId>
  <version>0.0.10</version>
</dependency>
```

With Gradle:

```groovy
compile(group: 'com.expedia.www', artifact: 'graphql-kotlin', version: '0.0.10')
```

## Generating a schema

`graphql-kotlin` provides a single function, `toSchema`, to generate a schema from Kotlin objects.

```kotlin
import graphql.schema.GraphQLSchema
import com.expedia.graphql.toSchema

class Query {
  fun getNumber() = 1
}

val schema: GraphQLSchema = toSchema(listOf(TopLevelObjectDef(Query())))
```

generates a `GraphQLSchema` with IDL that looks like this:

```graphql
type TopLevelQuery {
  getNumber: Int!
}
```

The `GraphQLSchema` generated can be used to expose a GraphQL API endpoint.

## Class `TopLevelObjectDef`

`toSchema` uses Kotlin reflection to build a GraphQL schema from given classes using `graphql-java`'s schema builder. We don't just pass a `KClass` though, we have to actually pass an object, because the functions on the object are transformed into the query or mutation's data fetchers. In most cases, a `TopLevelObjectDef` can be constructed with just an object:

```kotlin
class Query {
  fun getNumber() = 1
}

val def = TopLevelObjectDef(query)

toSchema(listOf(def))
```

In the above case, `toSchema` will use `query::class` as the reflection target, and `query` as the data fetcher target.

In a lot of cases, such as with Spring AOP, the object (or bean) being used to generate a schema is a dynamic proxy. In this case, `query::class` is not `Query`, but rather a generated class that will confuse the schema generator. To specify the `KClass` to use for reflection on a proxy, pass the class to `TopLevelObjectDef`:

```kotlin
@Component
class Query {
  @Timed
  fun getNumber() = 1
}

val def = TopLevelObjectDef(query, Query::class)

toSchema(listOf(def))
``` 

More about writing schemas with Kotlin below. All examples below are based on the example project included in this repo.

# Writing schemas with Kotlin

## Basics

`toSchema` requires a list of `TopLevelObjectDef` objects for both queries and mutations to be included in the GraphQL schema.

A query type is simply a Kotlin class that specifies *fields*, which can be functions or properties:

```kotlin
class WidgetQuery {
  fun widgetById(id: Int): Widget? {
    // grabs widget from a data source
  } 
}

class SimpleMutation: Mutation {

  private val data: MutableList<String> = mutableListOf()

  fun addToList(entry: String): MutableList<String> {
    data.add(entry)
    return data
  }
}
```

will generate:

```graphql
schema {
  query: TopLevelQuery
  mutation: TopLevelMutation
}

type TopLevelQuery {
    widgetById(id: Int!): Widget
}

type TopLevelMutation {
  addToList(entry: String!): [String!]!
}
```

Any `public` functions defined on a query or mutation Kotlin class will be translated into GraphQL fields on the object type. `toSchema` will recursively use Kotlin reflection to generate all object types, fields, arguments and enums.

### Types

For the most part, `graphql-kotlin` can directly map most Kotlin "primitive" types to standard GraphQL scalar types:

#### Scalars

| Kotlin Type         | GraphQL Type |
|---------------------|--------------|
| `kotlin.Int`        | `Int`        |
| `kotlin.Long`       | `Long`       |
| `kotlin.Short`      | `Short`      |
| `kotlin.Float`      | `Float`      |
| `kotlin.Double`     | `Float`      |
| `kotlin.BigInteger` | `BigInteger` |
| `kotlin.BigDecimal` | `BigDecimal` |
| `kotlin.Char`       | `Char`       |
| `kotlin.String`     | `String`     |
| `kotlin.Boolean`    | `Boolean`    |

`graphql-kotlin` also ships with a few extension scalar types:

#### Extension Scalars

By default, `graphql-kotlin` uses Kotlin reflections to generate all schema objects. If you want to apply custom behavior to the objects, you can define custom scalars and expose it to your schema using `com.expedia.graphql.schema.hooks.SchemaGeneratorHooks`. Example usage

```kotlin
class CustomSchemaGeneratorHooks: NoopSchemaGeneratorHooks() {

  override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {
    UUID::class -> graphqlUUIDType
    else -> null
  }
}

val graphqlUUIDType = GraphQLScalarType("UUID",
    "A type representing a formatted java.util.UUID",
    object: Coercing<UUID, String> { ... }
)
```

Once the scalars are registered you can use them anywhere in the schema as regular objects.


#### List Types

Both `kotlin.Array` and `kotlin.collections.List` are automatically mapped to the GraphQL `List` type. Type arguments provided to Kotlin collections are used as the type arguments in the GraphQL `List` type. 

```kotlin
class SimpleQuery {
  fun generateList(): List<Int> {
    val random = Random()
    return (1..10).map { random.nextInt(100) }.toList()
  }
}
```

The above Kotlin class would produce the following GraphQL schema:

```graphql
schema {
  query: TopLevelQuery
}

type TopLevelQuery {
  generateList: [Int!]!
}
```

#### Nullability

Both GraphQL and Kotlin have nullable as a marked typed so we can generated null safe schemas.

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
schema {
  query: TopLevelQuery
}

type TopLevelQuery {
  generateNullableNumber: Int

  generateNumber: Int!
}
```

### Fields

Any public fields on the returned objects will be exposed as part of the schema unless they are explicitly marked to be ignored with `@GraphQLIgnore` annotation. Documentation and deprecation information is also supported. For more details about different annotations see sections below.

```kotlin
@GraphQLDescription("A useful widget")
data class Widget(
    @property:GraphQLDescription("The widget's value that can be null")
    val value: Int?,
    @property:Deprecated(message = "This field is deprecated", replaceWith = ReplaceWith("value"))
    @property:GraphQLDescription("The widget's deprecated value that shouldn't be used")
    val deprecatedValue: Int? = value,
    @property:GraphQLIgnore
    val ignoredField: String? = "ignored",
    private val hiddenField: String? = "hidden"
)
```

The above Kotlin code would produce the following GraphQL object type:

```graphql
"""A useful widget"""
type Widget {
  """DEPRECATED: The widget's deprecated value that shouldn't be used"""
  deprecatedValue: Int @deprecated(reason: "This field is deprecated, replace with value")

  """The widget's value that can be null"""
  value: Int
```

### Arguments

Method arguments are automatically exposed as part of the arguments to the corresponding GraphQL fields.

```kotlin
class SimpleQuery{

  @GraphQLDescription("performs some operation")
  fun doSomething(@GraphQLDescription("super important value") value: Int): Boolean = true
}
```

The above Kotlin code will generate following GraphQL schema:

```graphql
type TopLevelQuery {
  """performs some operation"""
  doSomething(
    """super important value"""
    value: Int!
  ): Boolean!
}
```

This behavior is true for all arguments except for the GraphQL context objects. See section below for detailed information about `@GraphQLContext`.

### Enums

Enums are automatically mapped to GraphQL enum type.

```kotlin
enum class MyEnumType {
  ONE,
  TWO
}
```

Above enum will be generated as following GraphQL object

```graphql
enum MyEnumType {
  ONE
  TWO
}

```


### Interfaces

Functions returning interfaces will automatically expose all the types implementing this interface that are available on the classpath.

```kotlin
interface Animal {
    val type: AnimalType
    fun sound(): String
}

enum class AnimalType {
    CAT,
    DOG
}

class Dog: Animal {
    override val type: AnimalType
        get() = AnimalType.DOG

    override fun sound() = "bark"
    fun doSomethingUseful(): String = "something useful"
}

class Cat: Animal {
    override val type: AnimalType
        get() = AnimalType.CAT

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

Code above will produce the following GraphQL code

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
  doSomethingUseful: String!
  sound: String!
}

type TopLevelQuery {
  animal(
    type: AnimalType!
  ): Animal
}

```

### Unions

Unions are not supported.

## Subscriptions

TBD

## Annotations

`graphql-kotlin` ships with a number of annotation classes to allow you to enhance your GraphQL schema for things that can't be directly derived from Kotlin reflection.

### `@GraphQLContext`

All GraphQL servers have a concept of a "context". A GraphQL context contains metadata that is useful to the GraphQL server, but shouldn't necessarily be part of the GraphQL query's API. A prime example of something that is appropriate for the GraphQL context would be trace headers for an OpenTracing system such as [Haystack](https://expediadotcom.github.io/haystack). The GraphQL query itself does not need the information to perform its function, but the server itself needs the information to ensure observability.

The contents of the GraphQL context vary across GraphQL applications. For JVM based applications, `graphql-java` provides a context interface that can be extended.

Simply add `@GraphQLContext` to any argument to a field, and the GraphQL context for the environment will be injected. These arguments will be omitted by the schema generator.

```kotlin
class ContextualQuery {

    fun contextualQuery(
        value: Int,
        @GraphQLContext context: MyGraphQLContext
    ): ContextualResponse = ContextualResponse(value, context.myCustomValue)
}
```

The above query would produce the following GraphQL schema:

```graphql
schema {
  query: TopLevelQuery
}

type TopLevelQuery {
  contextualQuery(
    value: Int!
  ): ContextualResponse!
}
```

Note that the `@GraphQLContext` annotated argument is not reflected in the GraphQL schema.

### `@GraphQLIgnore`

There are two ways to ensure the GraphQL schema generation omits fields when using Kotlin reflection:

The first is by marking the field as `private` scope. The second method is by annotating the field with `@GraphQLIgnore`.

```kotlin
class SimpleQuery {
  @GraphQLIgnore
  fun notPartOfSchema() = "ignore me!"

  private fun privateFunctionsAreNotVisible() = "ignored private function"

  fun doSomething(
    value: Int
  ): Boolean {
    return true
  }
}
```

The above query would produce the following GraphQL schema:

```graphql
schema {
  query: TopLevelQuery
}

type TopLevelQuery {
  doSomething(value: Int!): Boolean!
}
```

Note that the public method `notPartOfSchema` is not included in the schema.

### `@GraphQLDescription`

Since Javadocs are not available at runtime for introspection, `graphql-kotlin` includes an annotation class `@GraphQLDescription` that can be used to add schema descriptions to *any* GraphQL schema element:

```kotlin
@GraphQLDescription("A useful widget")
data class Widget(
  @property:GraphQLDescription("The widget's value that can be null")
  val value: Int?
)

class WidgetQuery: Query {

  @GraphQLDescription("creates new widget for given ID")
  fun widgetById(@GraphQLDescription("The special ingredient") id: Int): Widget? = Widget(id)
}
```

The above query would produce the following GraphQL schema:

```graphql
schema {
  query: TopLevelQuery
}

"""A useful widget"""
type Widget {
  """The widget's value that can be null"""
  value: Int
}

type TopLevelQuery {
  """creates new widget for given ID"""
  widgetById(
    """The special ingredient"""
    id: Int!
  ): Widget
```

Note that the data class property is annotated as `@property:GraphQLDescription`. This is due to the way kotlin [maps back to the java elements](https://kotlinlang.org/docs/reference/annotations.html#annotation-use-site-targets). If you do not add the `property` prefix the annotation is actually on the contructor argument and will not be picked up by the generator.


### `@Deprecated`

GraphQL schemas can have fields marked as deprecated. Instead of creating a custom annotation, `graphql-kotlin` just looks for the `kotlin.Deprecated` annotation and will use the message for the deprecated reason.

```
class SimpleQuery {
  @Deprecated(message = "this query is deprecated", replaceWith = ReplaceWith("shinyNewQuery"))
  @GraphQLDescription("old query that should not be used always returns false")
  fun simpleDeprecatedQuery(): Boolean = false

  @GraphQLDescription("new query that always returns true")
  fun shinyNewQuery(): Boolean = true
}
```

The above query would produce the following GraphQL schema:

```graphql
schema {
  query: TopLevelQuery
}

type TopLevelQuery {

  """DEPRECATED: old query that should not be used always returns false"""
  simpleDeprecatedQuery: Boolean! @deprecated(reason: "this query is deprecated, replace with shinyNewQuery")

  """new query that always returns true"""
  shinyNewQuery: Boolean!
}
```

While you can deprecate any fields/methods in your code, GraphQL only supports deprecation directive on the queries, mutations and output types. All deprecated objects will have "DEPRECATED" prefix in their description.


### Custom directives

Custom directives can be added to the schema using custom annotations:

```kotlin
@GraphQLDirective(
        name = "Awesome",
        description = "This element is great",
        locations = [FIELD, FIELD_DEFINITION]
)
annotation class AwesomeDirective(val value: String)

class MyQuery {
    @AwesomeDirective("cool stuff")
    val somethingGreat: String = "Hello World"
}
```

The directive will then added to the schema as:

```graphql
# This element is great
directive @awesome(value: String) on FIELD | FIELD_DEFINITION

# Directives: Awesome 
type MyQuery {
   somethingGreat: String @awesome("cool stuff")
}
```

Directives can be added to various places in the schema, to see the full list see the [graphql.introspection.Introspection.DirectiveLocation enum](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/introspection/Introspection.java#L296) from graphql-java.

Note that GraphQL directives are currently not available through introspection. See: https://github.com/facebook/graphql/issues/300 and https://github.com/graphql-java/graphql-java/issues/1017 for more details.

#### Naming Convention

As described in the example above, the directive name in the schema will by default come from the `@GraphQLDirective.name` attribute.
If this value is not specified like an empty string, the directive name will be the name of the annotated annotation (eg: `AwesomeDirective`). 

For more readibility, the name used by the schema will be decapitalized so `Awesome` becomes `awesome` and `AwesomeDirective` would be `awesomeDirective`.

## Configuration

### Documentation Enforcement
