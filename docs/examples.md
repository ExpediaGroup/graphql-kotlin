---
id: examples
title: Examples
---

## Spring Example

One way to run a GraphQL server is with [Spring Boot](https://github.com/spring-projects/spring-boot). A sample Spring
Boot app that uses [Spring
Webflux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) together with
`graphql-kotlin-schema-generator` and [graphql-playground](https://github.com/prisma/graphql-playground) is provided as
a [examples/spring](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/spring). All the examples used
in this documentation should be available in the sample app.

In order to run it you can run
[Application.kt](https://github.com/ExpediaDotCom/graphql-kotlin/blob/master/graphql-kotlin-spring-example/src/main/kotlin/com/expedia/graphql/sample/Application.kt)
directly from your IDE. Alternatively you can also use the Spring Boot maven plugin by running `mvn spring-boot:run`
from the command line. Once the app has started you can explore the example schema by opening Playground endpoint at
[http://localhost:8080/playground](http://localhost:8080/playground).

## Federation Example

Example Spring Boot apps generating Federated GraphQL schema and Apollo Gateway that exposes single federated schema are
provided in [examples/federation](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/federation)
project. Please refer to their README files for details on how to run the target applications.
