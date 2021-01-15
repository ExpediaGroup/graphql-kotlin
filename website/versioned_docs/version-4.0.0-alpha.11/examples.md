---
id: version-4.0.0-alpha.11-examples
title: Examples
original_id: examples
---

A collection of example apps that use graphql-kotlin libraries to test and demonstrate usages can be found in the [examples module](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples).

## Client Example

A `graphql-kotlin-client` can be generated by using the provided Maven or Gradle. Example integration using Maven and
Gradle plugins can be found under the [examples/client](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/client)
folder.

## Federation Example

There is also an example of [Apollo Federation](https://www.apollographql.com/docs/apollo-server/federation/introduction/)
with two Spring Boot apps using `graphql-kotlin-federation` and an Apollo Gateway app in Nodejs that exposes a single
federated schema in [examples/federation](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/federation)
project. Please refer to the README files for details on how to run each application.

## Server Examples

Example integrations of `graphql-kotlin-schema-generator` with number of popular application frameworks can be found under
[examples/server](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/server) folder.

These examples also demonstrates how to include [`DataLoaders`](https://github.com/graphql-java/java-dataloader) in your query execution.

### Ktor Server Example

[Ktor](http://ktor.io/) is an asynchronous framework for creating microservices, web applications, and more. Example
integration can be found at [examples/server/ktor-server](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/server/ktor-server)

### Spring Server Example

One way to run a GraphQL server is with [Spring Boot](https://github.com/spring-projects/spring-boot). A sample Spring
Boot app that uses [Spring
Webflux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) together with
`graphql-kotlin-schema-generator` and [graphql-playground](https://github.com/prisma/graphql-playground) is provided as
a [examples/server/spring-server](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/server/spring-server).
All the examples used in this documentation should be available in this sample app.

In order to run it you can run
[Application.kt](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/Application.kt)
directly from your IDE. Alternatively you can also use the Spring Boot plugin from the command line.

```shell script
./gradlew bootRun
```

Once the app has started you can explore the example schema by opening Playground endpoint at
[http://localhost:8080/playground](http://localhost:8080/playground).