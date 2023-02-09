---
id: ktor-configuration
title: Ktor Plugin Configuration
---

`graphql-kotlin-ktor-server` plugin can be configured by using DSL when installing the plugin. Configuration is broken into multiple
groups related to specific functionality. See sections below for details.

```kotlin
install(GraphQL) {
    schema {
        // configuration that controls schema generation logic
    }
    engine {
        // configurations that control GraphQL execution engine
    }
    server {
        // configurations that control GraphQL HTTP server
    }
    routes {
        // routing configurations
    }
    tools {
        // configurations for various tools
    }
}
```

## Configuration Files

Ktor supports specifying configurations in `application.conf` (HOCON) or `application.yaml` file. By default, only HOCON format
is supported. To use a YAML configuration file, you need to add the `ktor-server-config-yaml` dependency to your project dependencies.
See [Ktor documentation](https://ktor.io/docs/configuration-file.html) for details.

:::caution
Not all configuration properties can be specified in your configuration file. You will need to use DSL to configure more advanced features
that cannot be represented in the property file syntax (e.g. any instantiated objects).
:::

All configuration options in `application.conf` format, with their default values are provided below.

```kotlin
graphql {
    schema {
        // this is a required property that you need to set to appropriate value
        // example value is just provided for illustration purposes
        packages = [
            "com.example"
        ]
        federation {
            enabled = false
            tracing {
                enabled = true
                debug = false
            }
        }
    }
    engine {
        automaticPersistedQueries {
            enabled = false
        }
        batching {
            enabled = false
            strategy = LEVEL_DISPATCHED
        }
        introspection {
            enabled = true
        }
    }
    server {
        streamingResponse = true
    }
    routes {
        endpoint = "graphql"
    }
    tools {
        graphiql {
            enabled = true
            endpoint = "graphiql"
        }
        sdl {
            enabled = true
            endpoint = "sdl"
            printAtStartup = false
        }
    }
}
```

## Schema Configuration

This section configures `graphql-kotlin-schema-generation` logic and is the **only** section that has to be configured.
At a minimum you need to configure the list of packages that can contain your GraphQL schema definitions and a list of queries.

All configuration options, with their default values are provided below.

```kotlin
schema {
    // this is a required property that you need to set to appropriate value
    // example value is just provided for illustration purposes
    packages = listOf("com.example")
    // non-federated schemas, require at least a single query
    queries = listOf()
    mutations = listOf()
    schemaObject = null
    // federated schemas require federated hooks
    hooks = NoopSchemaGeneratorHooks
    topLevelNames = TopLevelNames()
    federation {
        enabled = false
        tracing {
            enabled = true
            debug = false
        }
    }
}
```

## GraphQL Execution Engine Configuration

This section configures `graphql-java` execution engine that will be used to process your GraphQL requests.

All configuration options, with their default values are provided below.

```kotlin
engine {
    automaticPersistedQueries {
        enabled = false
    }
    // DO NOT enable default batching logic if specifying custom provider
    batching {
        enabled = false
        strategy = SYNC_EXHAUSTION
    }
    introspection {
        enabled = true
    }
    dataFetcherFactoryProvider = SimpleKotlinDataFetcherFactoryProvider()
    dataLoaderRegistryFactory = KotlinDataLoaderRegistryFactory()
    exceptionHandler = SimpleDataFetcherExceptionHandler()
    executionIdProvider = null
    idValueUnboxer = IDValueUnboxer()
    instrumentations = emptyList()
    // DO NOT specify custom provider if enabling default batching logic
    preparsedDocumentProvider = null
}
```

## Server Configuration

This section configures your GraphQL HTTP server.

All configuration options, with their default values are provided below.

```kotlin
server {
    contextProvider = DefaultKtorGraphQLContextFactory()
    jacksonConfiguration = { }
    requestParser = KtorGraphQLRequestParser(jacksonObjectMapper().apply(jacksonConfiguration))
    streamingResponse = true
}
```

## Routes Configuration

:::info
Subscriptions are currently not supported.
:::

This section configures your GraphQL HTTP routes.

All configuration options, with their default values are provided below.

```kotlin
routes {
    endpoint = "graphql"
}
```

## Tools Configuration

This section configures various GraphQL tools to improve your developer experience. Currently, we provide support for
[GraphiQL IDE](https://github.com/graphql/graphiql) and an SDL endpoint.

All configuration options, with their default values are provided below.

```kotlin
tools {
    graphiql {
        enabled = true
        endpoint = "graphiql"
    }
    sdl {
        enabled = true
        endpoint = "sdl"
        printAtStartup = false
    }
}
```
