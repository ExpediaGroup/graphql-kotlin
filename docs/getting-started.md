---
id: getting-started
title: Getting Started
---

GraphQL Kotlin is a collection of libraries, built on top of [graphql-java](https://www.graphql-java.com/), that aim to simplify running GraphQL in Kotlin

## Modules

* [examples](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples)
  &mdash; Example apps that use graphql-kotlin libraries to test and demonstrate usages
* [graphql-kotlin-client](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/graphql-kotlin-client) ([Javadoc](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-client))
  &mdash; Lightweight GraphQL Kotlin HTTP client
* [graphql-kotlin-schema-generator](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/graphql-kotlin-schema-generator) ([Javadoc](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-schema-generator))
  &mdash; Code only GraphQL schema generation for Kotlin
* [graphql-kotlin-federation](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/graphql-kotlin-federation) ([Javadoc](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-federation))
  &mdash; Schema generator extension to build federated GraphQL schemas
* [graphql-kotlin-spring-server](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/graphql-kotlin-spring-server) ([Javadoc](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-spring-server))
  &mdash; Spring Boot auto-configuration library to create GraphQL server
* [graphql-kotlin-types](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/graphql-kotlin-types) ([Javadoc](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-types))
&mdash; Core types used by both client and server
* [plugins](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/plugins)
  &mdash; GraphQL Kotlin Gradle and Maven plugins

If you encounter any problems using this library please open up a new
[Issue](https://github.com/ExpediaGroup/graphql-kotlin/issues)

Additional resources

* [GraphQL](https://graphql.org/)
* [graphql-java](https://www.graphql-java.com/documentation/)

## Installation

Using a JVM dependency manager, simply link any `graphql-kotlin-*` library to your project. You can see the latest
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

```groovy
compile(group: 'com.expediagroup', name: 'graphql-kotlin-spring-server', version: "$latestVersion")
```

## Running a Server
`graphql-kotlin-spring-server` is a combination of the schema generator and the server libraries. If you are looking to run a GraphQL server, this is the place to start.

See the docs in [Spring Server Overview](./spring-server/spring-overview.md).

## Creating a Client
`graphql-kotlin-plugins` can be used to generate a `graphql-kotlin-client` from an existing schema that is easy to use and type-safe.

See the docs in [Client Overview](./client/client-overview.md).

## Generating a Schema

While we have included a server implementation, you can use `graphql-kotlin-schema-generator` and `graphql-kotlin-federation` to generate a schema from Kotlin code and expose it with any server library.

See the docs in [Schema Generator Getting Started](./schema-generator/schema-generator-getting-started.md).
