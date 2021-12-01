---
id: custom-type-reference
title: Custom Types
---

Code-first has many advantages and removes duplication. However, one downside is that the types defined have to match
compiled Kotlin code. In some cases, it is possible to define a schema that is valid in SDL but it would be impossible to
return a Kotlin type that matches exactly that type. In these cases, you can pass in custom types in the schema
generator config and annotate the schema with the type info.

A common example is when you need to return a type or union defined in library JAR, but you can not change the code.
For example, let's say there is a type in a library. You can not change the fields, add annotations, or have it implement interfaces.

```kotlin
// Defined in external library
class Foo(val number: Int)
```

If you want to have this type be used in a new interface or union defined in your API, it is not possible to do in Kotlin code
since you can not modify the compiled code.

```kotlin
// New interface
interface TypeWithNumber { val number: Int }
// New union
interface TypeWithAnyField

// Error: We are not able to return Foo for any of these functions
fun customInterface(): TypeWithNumber = Foo(1)
fun customUnion(): TypeWithAnyField = Foo(1)
```

## `@GraphQLType`
You can use this annotation to change the return type of a field. The annotation accepts the type name, which will be
added as a type reference in the schema. This means that you will have to define the type and its schema with the same name in the configuration.

Doing this could still be serialization issues, so you should make sure that the data you return from the field matches the defined schema of the type.

```kotlin
// Defined in external library or can not be modified
class Foo(val number: Int)
class Bar(val value: String)

// Might return Foo or Bar
@GraphQLType("FooOrBar")
fun customUnion(): Any = if (Random.nextBoolean()) Foo(1) else Bar("hello")

// Will throw runtime error when serialized data does not match the schema
@GraphQLType("FooOrBar")
fun invalidType(): String = "hello"
```

### Custom Type Configuration
In our above example there is no Kotlin code for the type `FooOrBar`. It only exists by reference right now.
To add the type into the schema, specify the additional types in the [SchemaGeneratorConfiguration](./generator-config).
This is using the [grapqhl-java schema object builders](https://www.graphql-java.com/documentation/schema#union).


```kotlin
val fooCustom = GraphQLUnionType.newUnionType()
    .name("FooOrBar")
    .possibleType(GraphQLTypeReference("Foo"))
    .possibleType(GraphQLTypeReference("Bar"))
    .typeResolver { /* Logic for how to resolve types */ }
    .build()
val config = SchemaGeneratorConfig(supportedPackages, additionalTypes = setOf(fooCustom))
```

## Adding Missing Kotlin Types
In our above example, since the return type of the Kotlin code did not reference the Kotlin types `Foo` or `Bar`,
reflection will not pick those up by default. They will also need to be added as additional Kotlin types (`KType`) when generating the schema.

```kotlin
val generator = SchemaGenerator(config)
val schema = generator.use {
    it.generateSchema(
        queries = listOf(TopLevelObject(Query())),
        additionalTypes = setOf(
            Foo::class.createType(),
            Bar::class.createType(),
        )
    )
}
```

## Final Result
With all the above code, the final resulting schema should like this:

```graphql
type Query {
    customUnion: FooOrBar!
}

union FooOrBar = Foo | Bar
type Foo { number: Int! }
type Bar { value: String! }
```
