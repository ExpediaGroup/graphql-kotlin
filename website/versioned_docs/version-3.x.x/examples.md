---
id: examples
title: Examples
original_id: examples
---
A collection of example apps that use graphql-kotlin libraries to test and demonstrate usages can be found in the [examples module](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/examples).

## Client Example

A `graphql-kotlin-client` can be generated using the Maven and Gradle plugins in `graphql-kotlin-plugins`. For examples see the [examples/client](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/examples/client) folder.

## Spring Server Example

One way to run a GraphQL server is with [Spring Boot](https://github.com/spring-projects/spring-boot). A sample Spring
Boot app that uses [Spring
Webflux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) together with
`graphql-kotlin-schema-generator` and [graphql-playground](https://github.com/prisma/graphql-playground) is provided as
a [examples/spring](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/examples/spring). All the examples used
in this documentation should be available in the sample app.

In order to run it you can run
[Application.kt](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/Application.kt)
directly from your IDE. Alternatively you can also use the Spring Boot plugin from the command line.

```shell script

./gradlew bootRun

```

Once the app has started you can explore the example schema by opening Playground endpoint at
http:.

## Federation Example

There is also an example of [Apollo Federation](https://www.apollographql.com/docs/apollo-server/federation/introduction/) with two Spring Boot apps using `` and an Apollo Gateway app in Nodejs that exposes a single federated schema in [examples/federation](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/examples/federation)
project. Please refer to the README files for details on how to run each application.

## Spark Example

The spark example provides a demonstration of delivering a GraphQL service via the [Spark HTTP framework](http://sparkjava.com/). This example also demonstrates how to include [``](https://github.com/graphql-java/java-dataloader) in your query execution. This example can be found at [examples/spark](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/examples/spark)
