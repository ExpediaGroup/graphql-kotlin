# Graphql Kotlin SDL Generator

[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-sdl-generator.svg?label=Maven%20Central)](https://central.sonatype.com/search?namespace=com.expediagroup&q=name%3Agraphql-kotlin-sdl-generator)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-sdl-generator.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-sdl-generator)

GraphQL schema generator that invokes `graphql-kotlin-schema-generator` to generate GraphQL schema in Schema Definition
Language (SDL) format.

Generator scans classpath for classes implementing `graphql-kotlin-server` `Query`, `Mutation` and `Subscription` marker
interfaces. Since schema generation accepts an instance of `SchemaGeneratorHooks`, generator utilizes Java [ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html)
mechanism to dynamically load available `SchemaGeneratorHooksProvider` service providers from the classpath.

`SchemaGeneratorHooksProvider` provides an instance of `SchemaGeneratorHooks` that will be used to generate the schema.
If no provider is available on the classpath, SDL generator will default to use `NoopSchemaGeneratorHooks`. Since we need
to be able to deterministically choose a single hooks provider, generation of schema will fail if there are multiple providers
on the classpath.
