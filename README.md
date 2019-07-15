# GraphQL Kotlin

[![Build Status](https://travis-ci.org/ExpediaDotCom/graphql-kotlin.svg?branch=master)](https://travis-ci.org/ExpediaDotCom/graphql-kotlin)
[![codecov](https://codecov.io/gh/ExpediaDotCom/graphql-kotlin/branch/master/graph/badge.svg)](https://codecov.io/gh/ExpediaDotCom/graphql-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/com.expedia/graphql-kotlin.svg?label=maven%20central)](https://search.maven.org/artifact/com.expedia/graphql-kotlin)
[![Javadocs](https://img.shields.io/maven-central/v/com.expedia/graphql-kotlin.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expedia/graphql-kotlin)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

GraphQL Kotlin consists of number of libraries that aim to simplify GraphQL integration for Kotlin applications.

## Modules

* [graphql-kotlin-schema-generator](graphql-kotlin-schema-generator/README.md) - code only GraphQL schema generation for Kotlin
* [graphql-kotlin-spring-example](graphql-kotlin-spring-example/README.md) - example SpringBoot app that uses GraphQL Kotlin schema generator

## Documentation

Examples and documentation is available on our [Wiki](https://github.com/ExpediaDotCom/graphql-kotlin/wiki) or you can view the [javadocs](https://www.javadoc.io/doc/com.expedia/graphql-kotlin) for all published versions.

If you have a question about something you can not find in our wiki or javadocs, feel free to [create an issue](https://github.com/ExpediaDotCom/graphql-kotlin/issues) and tag it with the question label.

## Example

One way to run a GraphQL server is with Spring Boot. A sample Spring Boot app that uses [Spring Webflux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) together with `graphql-kotlin` and [graphql-playground](https://github.com/prisma/graphql-playground) is provided in the [example folder](https://github.com/ExpediaDotCom/graphql-kotlin/tree/master/example). All the examples used in this documentation should be available in the sample app.

In order to run it you can run [Application.kt](https://github.com/ExpediaDotCom/graphql-kotlin/blob/master/example/src/main/kotlin/com.expedia.graphql.sample/Application.kt) directly from your IDE. Alternatively you can also use the Spring Boot maven plugin by running `mvn spring-boot:run` from the command line. Once the app has started you can explore the example schema by opening the Playground endpoint at [http://localhost:8080/playground](http://localhost:8080/playground).
