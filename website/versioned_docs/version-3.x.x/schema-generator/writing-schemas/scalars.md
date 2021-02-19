---
id: scalars
title: Scalars
original_id: scalars
---
## Primitive Types

`graphql-kotlin-schema-generator` can directly map most Kotlin "primitive" types to standard GraphQL scalar types or
extended scalar types provided by `graphql-java`.

| Kotlin Type             | GraphQL Type     |
| ----------------------- | ---------------- |
| `kotlin.Int`     | `Int`     |
| `kotlin.Float`   | `Float`   |
| `kotlin.String`  | `String`  |
| `kotlin.Boolean` | `Boolean` |

&gt; NOTE: Extended GraphQL scalar types provided by `graphql-java` were [deprecated in v15](https://github.com/graphql-java/graphql-java/releases/tag/v15.0).
&gt; This includes the following types: `Long`, `Short`, `Float`, `BigInteger`, `BigDecimal`, and `Char`.
&gt; If you are currently using these types, they will be removed in future `graphql-java` releases.
&gt; See the [graphql-java-extended-scalars](https://github.com/graphql-java/graphql-java-extended-scalars) project if you need continued support.

## GraphQL ID

GraphQL supports the scalar type `ID`, a unique identifier that is not intended to be human readable. IDs are
serialized as a `String`. To expose a GraphQL `ID` field, you must use the `com.expediagroup.graphql.scalars.ID` class, which wraps the underlying `String` value.

&gt; NOTE: `graphql-java` supports additional types (`String`, `Int`, `Long`, or `UUID`) but [due to serialization issues](https://github.com/ExpediaGroup/graphql-kotlin/issues/317) we can only directly support Strings. You can still use a type like UUID internally just as long as you convert or parse the value yourself and handle the errors.

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
See the [Generator Configuration](../customizing-schemas/generator-config.md) documentation for more information.

Example usage

```kotlin

class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {

  override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {
    UUID::class -> graphqlUUIDType
    else -> null
  }
}

val graphqlUUIDType = GraphQLScalarType.newScalar()
    .name("UUID")
    .description("A type representing a formatted java.util.UUID")
    .coercing(UUIDCoercing)
    .build()

object UUIDCoercing : Coercing<UUID, String> {
    override fun parseValue(input: Any?): UUID = UUID.fromString(serialize(input))

    override fun parseLiteral(input: Any?): UUID? {
        val uuidString = (input as? StringValue)?.value
        return UUID.fromString(uuidString)
    }

    override fun serialize(dataFetcherResult: Any?): String = dataFetcherResult.toString()
}

```

Once the scalars are registered you can use them anywhere in the schema as regular objects.

## Common Issues

### Extended Scalars

By default, `graphql-kotlin` only supports the primitive scalar types listed above. If you are looking to use common java types as scalars, you need to include the [graphql-java-extended-scalars](https://github.com/graphql-java/graphql-java-extended-scalars) library and set up the hooks (see above), or write the logic yourself for how to resolve these custom scalars.

The most popular types that require extra configuration are: `LocalDate`, `DateTime`, `Instant`, `ZonedDateTime`, `URL`, `UUID`

### `TypeNotSupportedException`

If you see the following message `Cannot convert ** since it is not a valid GraphQL type or outside the supported packages ***`. This means that you need to update the [generator configuration](../customizing-schemas/generator-config.md) to include the package of your type or you did not properly set up the hooks to register the new type.
