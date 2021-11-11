---
id: custom-type-reference
title: Custom Types
---

Code-first has many advantages and removes duplication. However one downside is that the type you define do have to match
compiled Kotlin code. In some cases, it is possible to define a schema that is valid in SDL but it would be impossible to
return a Kotlin type that matches exactly that type. In these cases we have the ability to pass in custom types in the schema
generator config and annotate the schema with the type info.

## `@GraphQLType`
You can use this annotation to change the return type of any field. The annotaiton accepts the type name, which will be
added as a type reference in the schema. This means that you will have to define the type with the same name in the configuration.

There could still be serialization issues though so you should make sure that the data you return from the field still matches
the defined schema of the type.

A prime example of using this type is for custom unions.
```kotlin
// Might return Foo or Bar
class Query {
    @GraphQLType("FooOrBar")
    fun customType(): Any = if (Random.nextBoolean()) Foo(1) else Bar("hello")

    // Will throw runtime error when serialized data does not match the schema
    @GraphQLType("FooOrBar")
    fun invalidType(): String = "hello"
}

// Not exposed in the schema yet
class Foo(val number: Int)
class Bar(val value: String)
```

## Custom Type Configuration
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
reflection will not pick those up by default. They will need to be added as additional Kotlin types (`KType`) when generating the schema.

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
    customType: FooOrBar!
}

union FooOrBar = Foo | Bar
type Foo { number: Int! }
type Bar { value: String! }
```
