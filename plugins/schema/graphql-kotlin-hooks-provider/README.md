# GraphQL Kotlin Hooks Provider

This module provides `SchemaGeneratorHooksProvider` Service Provider Interface that is used by the GraphQL Kotlin Plugins
to obtain an instance of `SchemaGeneratorHooks` that will be used to generate GraphQL schema.

```kotlin
interface SchemaGeneratorHooksProvider {

    /**
     * Create a new instance of a SchemaGeneratorHooks that will be used to generate GraphQL schema in SDL format.
     */
    fun hooks(): SchemaGeneratorHooks
}
```

## Creating Custom Hooks Provider

SDL generator utilizes Java [ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html)
to load available service providers from the classpath.

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
`com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider` and contain single entry - a fully qualified
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
          |- com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider
```

Our service provider configuration file should have following content

```text
com.example.MyCustomSchemaGeneratorHooksProvider
```

## Limitations

We don't support Java 9 module mechanism for declaring `ServiceLoader` implementations. As a workaround, you have to define
your service providers in the provider configuration file under `META-INF/services`.
