---
id: getting-started 
title: Getting Started
---
## Installation

Using a JVM dependency manager, simply link `graphql-kotlin-schema-generator` to your project. You can see the latest
version and other examples in [Sonatype Central
Repository](https://search.maven.org/artifact/com.expediagroup/graphql-kotlin-schema-generator)

With Maven:

```xml
<dependency>
  <groupId>com.expediagroup</groupId>
  <artifactId>graphql-kotlin-schema-generator</artifactId>
  <version>${latestVersion}</version>
</dependency>
```

With Gradle:

```groovy
compile(group: 'com.expediagroup', name: 'graphql-kotlin-schema-generator', version: "$latestVersion")
```

## Generating a schema

`graphql-kotlin-schema-generator` provides a single function, `toSchema`, to generate a schema from Kotlin objects.

```kotlin
class Query {
  fun getNumber() = 1
}

val config = SchemaGeneratorConfig(listOf("com.expediagroup"))
val queries = listOf(TopLevelObject(Query()))
val schema: GraphQLSchema = toSchema(config = config, queries = queries)
```

generates a `GraphQLSchema` with IDL that looks like this:

```graphql
schema {
  query: Query
}

type Query {
  getNumber: Int!
}
```

The `GraphQLSchema` generated can be used to expose a GraphQL API endpoint.

### `toSchema`

This function accepts four arguments: `config`, `queries`, `mutations` and `subscriptions`. The `queries`, `mutations`
and `subscriptions` are a list of `TopLevelObject`s and will be used to generate corresponding GraphQL root types. See
below on why we use this wrapper class. The `config` contains all the extra information you need to pass, including
custom hooks, supported packages, and name overrides. See the full documentation [in the
wiki](https://github.com/ExpediaGroup/graphql-kotlin/wiki/Schema-Generator-Configuration)

You can see the definition for `toSchema` [in the
source](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/toSchema.kt)

### Class `TopLevelObject`

`toSchema` uses Kotlin reflection to build a GraphQL schema from given classes using `graphql-java`'s schema builder. We
don't just pass a `KClass` though, we have to actually pass an object, because the functions on the object are
transformed into the data fetchers. In most cases, a `TopLevelObject` can be constructed with just an object:

```kotlin
class Query {
  fun getNumber() = 1
}

val topLevelObject = TopLevelObject(Query())

toSchema(config = config, queries = listOf(topLevelObject))
```

In the above case, `toSchema` will use `topLevelObject::class` as the reflection target, and `Query` as the data fetcher
target.

In a lot of cases, such as with Spring AOP, the object (or bean) being used to generate a schema is a dynamic proxy. In
this case, `topLevelObject::class` is not `Query`, but rather a generated class that will confuse the schema generator.
To specify the `KClass` to use for reflection on a proxy, pass the class to `TopLevelObject`:

```kotlin
@Component
class Query {
  @Timed
  fun getNumber() = 1
}

val query = getObjectFromBean()
val customDef = TopLevelObject(query, Query::class)

toSchema(config, listOf(customDef))
```
