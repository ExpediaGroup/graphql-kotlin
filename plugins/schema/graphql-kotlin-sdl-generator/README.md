# Graphql Kotlin SDL Generator

GraphQL schema generator that invokes `graphql-kotlin-schema-generator` to generate GraphQL schema in Schema Definition
Language (SDL) format.

Generator scans classpath for classes implementing `graphql-kotlin-types` `Query`, `Mutation` and `Subscription` marker
interfaces. Since schema generation accepts an instance of `SchemaGeneratorHooks`, generator utilizes Java [ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html)
mechanism to dynamically load available `SchemaGeneratorHooksProvider` service providers from the classpath.

`SchemaGeneratorHooksProvider` provides an instance of `SchemaGeneratorHooks` that will be used to generate the schema.
If no provider is available on the classpath, SDL generator will default to use `NoopSchemaGeneratorHooks`. Since we need
to be able to deterministically choose a single hooks provider, generation of schema will fail if there are multiple providers
on the classpath.
