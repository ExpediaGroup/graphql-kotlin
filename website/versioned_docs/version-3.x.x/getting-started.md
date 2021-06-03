---
id: getting-started
title: Getting Started
original_id: getting-started
slug: /
---
GraphQL Kotlin is a collection of libraries, built on top of [graphql-java](https://www.graphql-java.com/), that aim to simplify running GraphQL in Kotlin

## Modules

-   [examples](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/examples)
    — Example apps that use graphql-kotlin libraries to test and demonstrate usages
-   [graphql-kotlin-client](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/graphql-kotlin-client) ([Javadoc](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-client))
    — Lightweight GraphQL Kotlin HTTP client
-   [graphql-kotlin-schema-generator](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/graphql-kotlin-schema-generator) ([Javadoc](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-schema-generator))
    — Code only GraphQL schema generation for Kotlin
-   [graphql-kotlin-federation](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/graphql-kotlin-federation) ([Javadoc](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-federation))
    — Schema generator extension to build federated GraphQL schemas
-   [graphql-kotlin-spring-server](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/graphql-kotlin-spring-server) ([Javadoc](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-spring-server))
    — Spring Boot auto-configuration library to create GraphQL server
-   [graphql-kotlin-types](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/graphql-kotlin-types) ([Javadoc](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-types))
    — Core types used by both client and server
-   [plugins](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/plugins)
    — GraphQL Kotlin Gradle and Maven plugins

If you encounter any problems using this library please open up a new
[Issue](https://github.com/ExpediaGroup/graphql-kotlin/issues)

Additional resources

-   [GraphQL](https://graphql.org/)
-   [graphql-java](https://www.graphql-java.com/documentation/)

## Installation

Using a JVM dependency manager, link any `graphql-kotlin-*` library to your project. You can see the latest
version and other examples in [Sonatype Central
Repository](https://search.maven.org/artifact/com.expediagroup/graphql-kotlin-spring-server)

### Maven

```xml

<dependency>
  <groupId>com.expediagroup</groupId>
  <artifactId>graphql-kotlin-spring-server</artifactId>
  <version>${latestVersion}</version>
</dependency>

```

### Gradle

```kotlin

implementation("com.expediagroup", "graphql-kotlin-spring-server", latestVersion)

```

## Generating a Schema

You can use `graphql-kotlin-schema-generator` to generate a schema from Kotlin code and expose it with any server library.

See the docs in [Schema Generator Getting Started](./schema-generator/schema-generator-getting-started.md).

### Apollo Federation

Using `graphql-kotlin-federation`, you can generate an [Apollo Federation](https://www.apollographql.com/docs/apollo-server/federation/federation-spec/) compliant schema.

See the docs in [Apollo Federation](./federated/apollo-federation.md).

## Running a Server

`graphql-kotlin-spring-server` is a combination of the schema generator, fedeation, and server libraries. If you are looking to run a GraphQL server, this is the place to start.

See the docs in [Spring Server Overview](./spring-server/spring-overview.md).

## Creating a Client

`graphql-kotlin-plugins` can be used to generate a `graphql-kotlin-client` from an existing schema that is easy to use and type-safe.

See the docs in [Client Overview](./client/client-overview.md).

## Examples

The `examples` module is a collection of working code and examples on how to use all of the `graphql-kotlin` modules.

See the [example docs](./examples.md) for more info.

## Blogs and Videos

You can find more posts and recorded conference talks on GraphQL and `graphql-kotlin` on our [Blogs and Videos](./blogs-and-videos.md) page.
