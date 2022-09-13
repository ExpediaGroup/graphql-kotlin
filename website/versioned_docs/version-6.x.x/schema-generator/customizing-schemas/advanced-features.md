---
id: advanced-features
title: Advanced Features
---
## Adding Custom Additional Types

There are a couple ways you can add more types to the schema without having them be directly consumed by a type in your schema.
This may be required for [Apollo Federation](../federation/apollo-federation.mdx), or maybe adding other interface implementations that are not picked up.

### `SchemaGenerator::generateSchema`

When generating a schema you can optionally specify additional types and input types to be included in the schema. This will
allow you to link to those types from your custom `SchemaGeneratorHooks` implementation using GraphQL reference instead of
manually creating the underlying GraphQL type.

```kotlin
val myConfig = SchemaGeneratorConfig(supportedPackages = listOf("com.example"))
val generator = SchemaGenerator(myConfig)

val schema = generator.generateSchema(
    queries = myQueries,
    additionalTypes = setOf(MyCustomObject::class.createType()),
    additionalInputTypes = setOf(MyCustomInputObject::class.createType())
)
```

### `SchemaGenerator::addAdditionalTypesWithAnnotation`

This method is protected so if you override the `SchemaGenerator` used you can call this method to add types that have a specific annotation.
You can see how this is used in `graphql-kotlin-federation` as [an example](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/FederatedSchemaGenerator.kt).
