---
id: scalars
title: Scalars
---
## Primitive Types

`graphql-kotlin-schema-generator` can directly map most Kotlin "primitive" types to standard GraphQL scalar types or
extended scalar types provided by `graphql-java`.

| Kotlin Type       | GraphQL Type |
|-------------------| ------------ |
| `kotlin.String`   | `String`  |
| `kotlin.Boolean`  | `Boolean` |
| `kotlin.Int`      | `Int`     |
| `kotlin.Double`   | `Float`   |
| `kotlin.Float`    | `Float`   |

:::note
The GraphQL spec uses the term `Float` for signed double‐precision fractional values. `graphql-java` maps this to a `java.lang.Double` for the execution. The generator will map both `kotlin.Double` and `kotlin.Float` to GraphQL `Float` but we recommend you use `kotlin.Double`.
:::

## GraphQL ID

GraphQL supports the scalar type `ID`, a unique identifier that is not intended to be human-readable. IDs are
serialized as a `String`. To expose a GraphQL `ID` field, you must use the `com.expediagroup.graphql.generator.scalars.ID`
class, which is an *inline value class* that wraps the underlying `String` value.

:::note
`graphql-java` supports additional types (`String`, `Int`, `Long`, or `UUID`) but [due to serialization issues](https://github.com/ExpediaGroup/graphql-kotlin/issues/317) we can only directly support Strings.
:::

Since `ID` is a value class, it may be represented at runtime as a wrapper or directly as underlying type. Due to the generic
nature of the query processing logic we *always* end up with up a wrapper type when resolving the field value. As a result,
in order to ensure that underlying scalar value is correctly serialized, we need to explicitly unwrap it by registering
`IDValueUnboxer` with your GraphQL instance.

```kotlin
// registering custom value unboxer
val graphQL = GraphQL.newGraphQL(graphQLSchema)
    .valueUnboxer(IDValueUnboxer())
    .build()
```

:::note
`IDValueUnboxer` bean is automatically configured by `graphql-kotlin-spring-server`.
:::

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
    override fun parseValue(input: Any): UUID = runCatching {
        UUID.fromString(serialize(input))
    }.getOrElse {
        throw CoercingParseValueException("Expected valid UUID but was $input")
    }

    override fun parseLiteral(input: Any): UUID {
        val uuidString = (input as? StringValue)?.value
        return runCatching {
            UUID.fromString(uuidString)
        }.getOrElse {
            throw CoercingParseLiteralException("Expected valid UUID literal but was $uuidString")
        }
    }

    override fun serialize(dataFetcherResult: Any): String = runCatching {
        dataFetcherResult.toString()
    }.getOrElse {
        throw CoercingSerializeException("Data fetcher result $dataFetcherResult cannot be serialized to a String")
    }
}
```

Once the scalars are registered you can use them anywhere in the schema as regular objects.

### Inline Value Classes

It is often beneficial to create a wrapper around the underlying primitive type to better represent its meaning. Inline value classes can be used
to optimize such use cases - Kotlin compiler will attempt to use underlying type directly whenever possible and only keep the wrapper classes
whenever it is necessary.

:::note
Nullable value class types may result in a runtime `IllegalArgumentException` due to https://youtrack.jetbrains.com/issue/KT-31141. This should be resolved in Kotlin 1.7.0+.
:::

#### Representing Unwrapped Value Classes in the Schema as the Underlying Type

In order to represent unwrapped inline value classes in your schema as the underlying type, you need to register it using hooks and also provide value unboxer that will be used by
`graphql-java` when dealing with its wrapper object.

```kotlin
@JvmInline
value class MyValueClass(
    val value: String
)

class MyQuery : Query {
    fun inlineValueClassQuery(value: MyValueClass? = null): MyValueClass = value ?: MyValueClass("default")
}

class MySchemaGeneratorHooks : SchemaGeneratorHooks {
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        MyValueClass::class -> Scalars.GraphQLString
        else -> null
    }
}

class MyValueUnboxer : IDValueUnboxer() {
    override fun unbox(value: Any?): Any? = when (value) {
        is MyValueClass -> `object`.value
        else -> super.unbox(`object`)
    }
}

val config = SchemaGeneratorConfig(
    supportedPackages = listOf("com.example"),
    hooks = MySchemaGeneratorHooks()
)
val schema = toSchema(
    config = config,
    queries = listOf(TopLevelObject(MyQuery()))
)
val graphQL = GraphQL.newGraphQL(graphQLSchema)
    .valueUnboxer(MyValueUnboxer())
    .build()
```

This will generate a schema that exposes value classes as the corresponding wrapped type:

```graphql
type Query {
  inlineValueClassQuery(value: String): String!
}
```

:::note
GraphQL ID scalar type is represented using inline value class. When registering additional inline value classes you should extend the `IDValueUnboxer` to ensure IDs will be correctly processed. Alternatively, extend `DefaultValueUnboxer` and handle the `ID` value class as above.

If you are using `graphql-kotlin-spring-server` you should create an instance of your bean as

```kotlin
@Bean
fun idValueUnboxer(): IDValueUnboxer = MyValueUnboxer()
```
:::

#### Representing Unwrapped Value Classes in the Schema as a Custom Scalar Type

In many cases, it may be useful to represent value classes in the schema as a custom scalar type, as the additional type information is often useful for clients. In this form, the value class is unwrapped, but uses a custom scalar type to preserve the extra type information.

To do this, define a coercer for the value class that transforms it to and from the underlying type, and register it with the custom schema hooks:

```kotlin
val graphqlMyValueClassType: GraphQLScalarType = GraphQLScalarType.newScalar()
  .name("MyValueClass")
  .description(
    """
    |Represents my value class as a String value.
    |""".trimMargin()
  )
  .coercing(MyValueClassCoercing)
  .build()

object MyValueClassCoercing : Coercing<MyValueClass, String> {
  override fun parseValue(input: Any): MyValueClass = ...
  override fun parseLiteral(input: Any): MyValueClass = ...
  override fun serialize(dataFetcherResult: Any): String = ...
}

class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {
  override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {
    MyValueClass::class -> graphqlMyValueClassType
    else -> null
  }
}
```

This will generate the schema that exposes value classes as a scalar type:

```graphql
scalar MyValueClass

type Query {
  inlineValueClassQuery(value: MyValueClass): MyValueClass!
}
```

#### Representing Value Classes in the Schema as Objects

To do this, simply use the value class directly without defining any coercers or unboxers as in the previous sections.

This will generate the schema that exposes value classes as a wrapped type, similar to a regular class:

```graphql
input MyValueClassInput {
    value: String!
}

type MyValueClass {
    value: String!
}

type Query {
  inlineValueClassQuery(value: MyValueClassInput): MyValueClass!
}
```

## Common Issues

### Extended Scalars

By default, `graphql-kotlin` only supports the primitive scalar types listed above. If you are looking to use common java types as scalars, you need to include the [graphql-java-extended-scalars](https://github.com/graphql-java/graphql-java-extended-scalars) library and set up the hooks (see above), or write the logic yourself for how to resolve these custom scalars.

The most popular types that require extra configuration are: `LocalDate`, `DateTime`, `Instant`, `ZonedDateTime`, `URL`, `UUID`

### `TypeNotSupportedException`

If you see the following message `Cannot convert ** since it is not a valid GraphQL type or outside the supported packages ***`. This means that you need to update the [generator configuration](../customizing-schemas/generator-config.md) to include the package of your type or you did not properly set up the hooks to register the new type.
