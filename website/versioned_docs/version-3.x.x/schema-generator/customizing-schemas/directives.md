---
id: directives
title: Directives
original_id: directives
---
GraphQL directives can be used to transform the schema types, fields and arguments as well as modify the runtime
behavior of the query (e.g. implement access control, etc). Common use cases involve limiting functionality based on the
user authentication and authorization. While [GraphQL
spec](https://graphql.github.io/graphql-spec/draft/#sec-Type-System.Directives) specifies two types of directives -
`executable` (aka query) and `type system` (aka schema) directives, only the latter one is supported by
`graphql-kotlin-schema-generator`.

## Default Directives

`@deprecated` - schema directive used to represent deprecated portion of the schema.
See [@Deprecated](deprecating-schema.md) annotation documentation for more details

```graphql

type Query {
  deprecatedQuery: Boolean! @deprecated(reason: "No longer supported")
}

```

`@skip` - query directive that allows for conditional exclusion of fields or fragments

```graphql

query myQuery($shouldSkip: Boolean) {
  myField @skip(if: $shouldSkip)
}

```

`@include` - query directive that allows for conditional inclusion of fields or fragments

```graphql

query myQuery($shouldInclude: Boolean) {
  myField @include(if: $shouldInclude)
}

```

## Custom Directives

Custom directives can be added to the schema using custom annotations:

```kotlin

@GraphQLDirective(
        name = "awesome",
        description = "This element is great",
        locations = [FIELD_DEFINITION]
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
directive @awesome(value: String) on FIELD_DEFINITION

type MyQuery {
   somethingGreat: String @awesome("cool stuff")
}

```

Directives can be added to various places in the schema. See the
[graphql.introspection.Introspection.DirectiveLocation](https://github.com/graphql-java/graphql-java/blob/v13.0/src/main/java/graphql/introspection/Introspection.java#L332)
enum from `graphql-java` for a full list of valid locations.

**Note that GraphQL directives are currently not available through introspection and you have to use SDL directly
instead (you can use convenient `print` extension function of `GraphQLSchema`)**. See [GraphQL
issue](https://github.com/facebook/graphql/issues/300) and corresponding [graphql-java
issue](https://github.com/graphql-java/graphql-java/issues/1017) for more details about the introspection issue.

### Naming Convention

As described in the example above, the directive name in the schema will by default come from the
`@GraphQLDirective.name` attribute which should follow `lowerCamelCase` format. If this value is not specified, the
directive name will default to the normalized decapitalized name of the annotated annotation (eg: `awesomeDirective` in
the example above).

### Customizing Behavior

Directives allow you to customize the behavior of your schema based on some predefined conditions. Simplest way to
modify the default behavior of your GraphQLTypes is by providing your custom `KotlinSchemaDirectiveWiring` through
`KotlinDirectiveWiringFactory` factory used by your `SchemaGeneratorHooks`.

Example of a directive that converts field to lowercase

```kotlin

@GraphQLDirective(name = "lowercase", description = "Modifies the string field to lowercase")
annotation class LowercaseDirective

class LowercaseSchemaDirectiveWiring : KotlinSchemaDirectiveWiring {

    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher: DataFetcher<Any> = environment.getDataFetcher()

        val lowerCaseFetcher = DataFetcherFactories.wrapDataFetcher(
            originalDataFetcher,
            BiFunction<DataFetchingEnvironment, Any, Any>{ _, value -> value.toString().toLowerCase() }
        )
        environment.setDataFetcher(lowerCaseFetcher)
        return field
    }
}

```

While you can manually apply all the runtime wirings to the corresponding GraphQL types directly in
`SchemaGeneratorHooks#onRewireGraphQLType`, we recommend the usage of our
[KotlinDirectiveWiringFactory](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/directives/KotlinDirectiveWiringFactory.kt)
to simplify the integrations. `KotlinDirectiveWiringFactory` accepts a mapping of directives to corresponding wirings or
could be extended to provide the wirings through `KotlinDirectiveWiringFactory#getSchemaDirectiveWiring` that accepts
`KotlinSchemaDirectiveEnvironment`.

```kotlin

val queries = ...
val customWiringFactory = KotlinDirectiveWiringFactory(
    manualWiring = mapOf<String, KotlinSchemaDirectiveWiring>("lowercase" to LowercaseSchemaDirectiveWiring()))
val customHooks = object : SchemaGeneratorHooks {
    override val wiringFactory: KotlinDirectiveWiringFactory
        get() = customWiringFactory
}
val schemaGeneratorConfig = SchemaGeneratorConfig(hooks = customHooks)
val schema = toSchema(queries = queries, config = schemaGeneratorConfig)

```

While providing directives on different schema elements you will be able to modify the underlying GraphQL types. Keep in
mind though that data fetchers are used to resolve the fields so only field directives (and by association their
arguments directives) can modify runtime behavior based on the context and user input.

**NOTE: `graphql-kotlin` prioritizes manual wiring mappings over the wirings provided by the
`KotlinDirectiveWiringFactory#getSchemaDirectiveWiring`. This is a different behavior than `graphql-java` which will
first attempt to use `WiringFactory` and then fallback to manual overrides.**

For more details please refer to the example usage of directives in our [example
app](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/examples/spring).

## Directive Chaining

Directives are applied in the order annotations are declared on the given object. Given

```kotlin

  @Directive1
  @Directive2
  fun doSomething(): String {
    // does something
  }

```

`Directive1` will be applied first followed by the `Directive2`.

## Ignoring Directive Arguments

Normally if you wanted to exclude a field or argument from the schema, you could use [@GraphQLIgnore](./excluding-fields.md).
However, due to reflection and kotlin limitations, the generated JVM code for interface arguments can only have annotations on getters.

This is easily fixable though using the [`@get:` target prefix](https://kotlinlang.org/docs/reference/annotations.html#annotation-use-site-targets)
See [graphql-kotlin#763](https://github.com/ExpediaGroup/graphql-kotlin/pull/763) for more details.

```kotlin

@GraphQLDirective
annotation class DirectiveWithIgnoredArgs(
    val string: String,

    @get:GraphQLIgnore
    val ignoreMe: String
)

```

This will generate the following schema

```graphql

directive @directiveWithIgnoredArgs(
  string: String!
) on ...

```
