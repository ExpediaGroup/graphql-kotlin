---
id: hooks-provider
title: Schema Generator Hooks Provider
---
GraphQL Kotlin plugins can generate GraphQL schema as your build artifact directly from your source code. Plugins will scan
your classpath for classes implementing `graphql-kotlin-types` marker `Query`, `Mutation` and `Subscription` interfaces
and then generate corresponding GraphQL schema using `graphql-kotlin-schema-generator`. By default, plugins will generate
the schema using `NoopSchemaGeneratorHooks`. If your project uses custom hooks or needs to generate the federated GraphQL
schema, you will need to provide an instance of `SchemaGeneratorHooksProvider` that will be used to create an instance of
your custom hooks.

`SchemaGeneratorHooksProvider` is a service provider interface that exposes a single `hooks` method to generate an instance
of `SchemaGeneratorHooks` that will be used to generate your schema. By utilizing Java [ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html)
we can dynamically load your custom provider from the classpath. Service provider can be provided as part of your project
sources, included inside of one of your project dependencies or through explicitly provided artifact. Since we need to be
able to deterministically choose a single hooks provider, generation of schema will fail if there are multiple providers
on the classpath.

## Creating Custom Hooks Service Provider

### Add dependency on graphql-kotlin-hooks-provider

`SchemaGeneratorHooksProvider` interface is defined in `graphql-kotlin-hooks-provider` module.

DOCUSAURUS_CODE_TABS

Gradle

```kotlin

// build.gradle.kts
implementation("com.expediagroup", "graphql-kotlin-hooks-provider", latestVersion)

```

Maven

```xml

<dependency>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-hooks-provider</artifactId>
    <version>${latestVersion}</version>
</dependency>

```

END_DOCUSAURUS_CODE_TABS

### Create new SchemaGeneratorHooksProvider implementation

Service provider implementation has to implement `SchemaGeneratorHooksProvider` interface that provides a way to instantiate
schema generator hooks that will be used to generate the GraphQL schema.

```kotlin

package com.example

class MyCustomSchemaGeneratorHooksProvider : SchemaGeneratorHooksProvider {
    override fun hooks(): SchemaGeneratorHooks = MyCustomHooks()
}

```

### Create provider configuration file

Service loader provider configuration file should be created under JAR `/META-INF/services` directory (e.g. `src/main/resources/META-INF/services`
in default project structure). Name of the provider configuration should be fully qualified service provider interface name, i.e.
`SchemaGeneratorHooksProvider` and contain single entry - a fully qualified
name of the service provider implementation.

Using the example service provider implementation from the above, our project structure should look like

```

my-project
|- src
  |- main
    |- kotlin
      |- com
        |- example
          |- MyCustomSchemaGeneratorHooksProvider.kt
    |- resources
      |- META-INF
        |- services
          |- SchemaGeneratorHooksProvider

```

Our service provider configuration file should have following content

```text

com.example.MyCustomSchemaGeneratorHooksProvider

```

## Limitations

We don't support Java 9 module mechanism for declaring `ServiceLoader` implementations. As a workaround, you have to define
your service providers in the provider configuration file under `META-INF/services`.
