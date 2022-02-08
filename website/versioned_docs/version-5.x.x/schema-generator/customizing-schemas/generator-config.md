---
id: generator-config
title: Generator Configuration & Hooks
---

`graphql-kotlin-schema-generator` provides a single function, `toSchema,` to generate a schema from Kotlin objects. This
function accepts four arguments: config, queries, mutations and subscriptions.

## TopLevelObjects

The queries, mutations and subscriptions are a list of
[TopLevelObjects](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/TopLevelObject.kt)
and will be used to generate corresponding GraphQL root types.

## SchemaGeneratorConfig

The [config](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/SchemaGeneratorConfig.kt)
contains all the extra information you need to pass, including custom hooks, supported packages and name overrides.
`SchemaGeneratorConfig` has some default settings but you can override them and add custom behaviors for generating your
schema.

- `supportedPackages` **[Required]** - List of Kotlin packages that can contain schema objects. Limits the scope of
    packages that can be scanned using reflections.
- `topLevelNames` _[Optional]_ - Set the name of the top level GraphQL fields, defaults to `Query`, `Mutation` and
    `Subscription`
- `hooks` _[Optional]_ - Set custom behaviors for generating the schema, see below for details.
- `dataFetcherFactory` _[Optional]_ - Sets custom behavior for generating data fetchers
- `introspectionEnabled` _[Optional]_ - Boolean flag indicating whether introspection queries are enabled, introspection queries are enabled by default
- `additionalTypes` _[Optional]_ - Set of additional GraphQL types to include when generating the schema.

## SchemaGeneratorHooks

Hooks are lifecycle events that are called and triggered while the schema is building that allow users to customize the
schema.

For exact names and details of every hook, see the comments and descriptions in our latest
[javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-schema-generator) or directly in the source file:
[SchemaGeneratorHooks.kt](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/hooks/SchemaGeneratorHooks.kt)

As an example here is how you would write a custom hook and provide it through the configuration

```kotlin
class MyCustomHooks : SchemaGeneratorHooks {
  // Only generate functions that start with "dog"
  // This would probably be better just to use @GraphQLIgnore, but this is just an example
  override fun isValidFunction(function: KFunction<*>) = function.name.startsWith("dog")
}

class Query {
  fun dogSound() = "bark"

  fun catSound() = "meow"
}

val config = SchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = MyCustomHooks())

val queries = listOf(TopLevelObject(Query()))

toSchema(queries = queries, config = config)
```

will generate

```graphql
schema {
  query: Query
}

type Query {
  dogSound: String!
}
```

Notice there is no `catSound` function.
