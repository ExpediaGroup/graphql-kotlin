---
id: scalars
title: Scalars
---

`graphql-kotlin-schema-generator` can directly map most Kotlin "primitive" types to standard GraphQL scalar types or
extended scalar types provided by `graphql-java`.

| Kotlin Type         | GraphQL Type |
|---------------------|--------------|
| `kotlin.Int`        | `Int`        |
| `kotlin.Float`      | `Float`      |
| `kotlin.String`     | `String`     |
| `kotlin.Boolean`    | `Boolean`    |
| | **Extended GraphQL Types**       |
| `kotlin.Long`       | `Long`       |
| `kotlin.Short`      | `Short`      |
| `kotlin.Double`     | `Float`      |
| `kotlin.BigInteger` | `BigInteger` |
| `kotlin.BigDecimal` | `BigDecimal` |
| `kotlin.Char`       | `Char`       |

> NOTE: Extended GraphQL scalar types provided by `graphql-java` are generated as custom scalar types. When using those custom scalar types your GraphQL clients will have to know how to correctly parse and serialize them. See `graphql-java` [documentation](https://www.graphql-java.com/documentation/v14/scalars/) for more details.

## ID

GraphQL supports the scalar type `ID`, a unique identifier that is not intended to be human readable. IDs are
serialized as a `String`. To expose a GraphQL `ID` field, you must use the `com.expediagroup.graphql.types.ID` class, which wraps the underlying `String` value.


> NOTE: `graphql-java` supports additional types (`String`, `Int`, `Long`, or `UUID`) but [due to serialization issues](https://github.com/ExpediaGroup/graphql-kotlin/issues/317) we can only directly support Strings. You can still use a type like UUID internally just as long as you convert or parse the value yourself and handle the errors.

```kotlin
data class Person(
    val id: ID,
    val name: String
)

fun findPersonById(id: ID) = Person(id, "John Smith")

fun generateRandomId(): ID = ID(UUID.randomUUID().toString())
```

This would produce the following schema:

```graphql
schema {
    query: Query
}


type Query {
    findPersonById(id: ID!): Person!
    generateRandomId: ID!
}

type Person {
    id: ID!
    name: String!
}
```

## Custom Scalars

By default, `graphql-kotlin-schema-generator` uses Kotlin reflections to generate all schema objects. If you want to
apply custom behavior to the objects, you can also define your own custom scalars. Custom scalars have to be explicitly
added to the schema through `SchemaGeneratorHooks.willGenerateGraphQLType`.
See the [Generator Configuration](customizing-schemas/generator-config.md) documentation for more information.

Example usage

```kotlin
class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {

  override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {
    URL::class -> graphqlURLType
    else -> null
  }
}

val graphqlURLType = GraphQLScalarType("URL",
    "A type representing a formatted java.net.URL",
    object: Coercing<URL, String> { ... }
)
```

Once the scalars are registered you can use them anywhere in the schema as regular objects.
