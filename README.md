# GraphQL Kotlin

[![Continuous Integration](https://github.com/ExpediaGroup/graphql-kotlin/workflows/Continuous%20Integration/badge.svg)](https://github.com/ExpediaGroup/graphql-kotlin/actions?query=workflow%3A%22Continuous+Integration%22)
[![Publish Docs](https://github.com/ExpediaGroup/graphql-kotlin/workflows/Publish%20Docs/badge.svg)](https://github.com/ExpediaGroup/graphql-kotlin/actions?query=workflow%3A%22Publish+Docs%22)

GraphQL Kotlin is a collection of libraries, built on top of [graphql-java](https://www.graphql-java.com/), that aim to simplify running GraphQL clients and servers in Kotlin.

Visit our [documentation site](https://expediagroup.github.io/graphql-kotlin) for more details.

## 📦 Modules

* [clients](/clients) - Lightweight GraphQL Kotlin HTTP clients based on Ktor HTTP client and Spring WebClient
* [examples](/examples) - Example apps that use graphql-kotlin libraries to test and demonstrate usages
* [graphql-kotlin-federation](/graphql-kotlin-federation) - Schema generator extension to build Apollo Federation GraphQL schemas
* [graphql-kotlin-schema-generator](/graphql-kotlin-schema-generator) - Code only GraphQL schema generation for Kotlin
* [graphql-kotlin-spring-server](/graphql-kotlin-spring-server) - Spring Boot auto-configuration library to create a GraphQL server
* [graphql-kotlin-types](/graphql-kotlin-types) - Core types used by both client and server
* [plugins](/plugins) - Gradle and Maven plugins

## ⌨️ Usage

While all the individual modules of `graphql-kotlin` are published as stand-alone libraries, the most common use cases are running a server, and genereating a type-safe client.

### Server Example

A basic example of how you can use [graphql-kotlin-spring-server](/graphql-kotlin-spring-server) to run a GraphQL server can be found on our [server documentation section](https://expediagroup.github.io/graphql-kotlin/docs/spring-server/spring-overview).

### Client Example

A basic setup of [graphql-kotlin-client](/clients/graphql-kotlin-client) can be found on our [client documentation section](https://expediagroup.github.io/graphql-kotlin/docs/client/client-overview).

## 📋 Documentation

More examples and documentation are available on our [documentation site](https://expediagroup.github.io/graphql-kotlin) hosted in GitHub Pages. We also have the [examples](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples) module which can be run locally for testing and shows example code using the libraries.

If you have a question about something you can not find in our documentation, the indivdual module READMEs, or [javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-schema-generator), feel free to contribute to the docs or [start a disucssion](https://github.com/ExpediaGroup/graphql-kotlin/discussions) and tag it with the question label.

If you would like to contribute to our documentation see the [website](/website) directory for more information.

## 🗞 Blog Posts and Videos

The [Blogs & Videos page](https://expediagroup.github.io/graphql-kotlin/docs/blogs-and-videos) in the GraphQL Kotlin documentation links to blog posts, release announcements, conference talks about the library, and general talks about GraphQL at Expedia Group.

## 👥 Contact

This project is part of Expedia Group Open Source but also maintained by a dedicated team

* Expedia Group OSS
  * https://expediagroup.github.io
  * oss@expediagroup.com

* GraphQL Kotlin Committers
  * Github team: `@ExpediaGroup/graphql-kotlin-committers`

If you have a specific question about the library or code, please [start a disucssion](https://github.com/ExpediaGroup/graphql-kotlin/discussions) for the community.

We also have a public channel, ([#graphql-kotlin](https://app.slack.com/client/T09229ZC6/CQLNT7B29)), open on the Kotlin Slack instance ([kotlinlang.slack.com](https://kotlinlang.slack.com)).
See the info [here on how to join this slack instance](https://slack.kotlinlang.org/).

## ✏️ Contributing

To get started, please fork the repo and checkout a new branch. You can then build the library locally with Gradle

```shell script
./gradlew clean build
```

See more info in [CONTRIBUTING.md](CONTRIBUTING.md).

After you have your local branch set up, take a look at our [open issues](https://github.com/ExpediaGroup/graphql-kotlin/issues) to see where you can contribute.

## 🛡️ Security

For more info on how to contact the team for security issues or the supported versions that receive security updates, see [SECURITY.md](./.github/SECURITY.md)

## ⚖️ License

This library is licensed under the [Apache License, Version 2.0](LICENSE)
