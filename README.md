# GraphQL Kotlin

[![Continuous Integration](https://github.com/ExpediaGroup/graphql-kotlin/workflows/Continuous%20Integration/badge.svg)](https://github.com/ExpediaGroup/graphql-kotlin/actions?query=workflow%3A%22Continuous+Integration%22)
[![Publish Docs](https://github.com/ExpediaGroup/graphql-kotlin/workflows/Publish%20Latest%20Docs/badge.svg)](https://github.com/ExpediaGroup/graphql-kotlin/actions?query=workflow%3A%22Publish+Latest+Docs%22)
[![Discussions](https://img.shields.io/badge/Discussions-On%20GitHub-blue)](https://github.com/ExpediaGroup/graphql-kotlin/discussions)
[![Slack](https://img.shields.io/badge/Slack-%23graphql--kotlin-ECB22E.svg?logo=data:image/svg+xml;base64,PHN2ZyB2aWV3Qm94PSIwIDAgNTQgNTQiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48cGF0aCBkPSJNMTkuNzEyLjEzM2E1LjM4MSA1LjM4MSAwIDAgMC01LjM3NiA1LjM4NyA1LjM4MSA1LjM4MSAwIDAgMCA1LjM3NiA1LjM4Nmg1LjM3NlY1LjUyQTUuMzgxIDUuMzgxIDAgMCAwIDE5LjcxMi4xMzNtMCAxNC4zNjVINS4zNzZBNS4zODEgNS4zODEgMCAwIDAgMCAxOS44ODRhNS4zODEgNS4zODEgMCAwIDAgNS4zNzYgNS4zODdoMTQuMzM2YTUuMzgxIDUuMzgxIDAgMCAwIDUuMzc2LTUuMzg3IDUuMzgxIDUuMzgxIDAgMCAwLTUuMzc2LTUuMzg2IiBmaWxsPSIjMzZDNUYwIi8+PHBhdGggZD0iTTUzLjc2IDE5Ljg4NGE1LjM4MSA1LjM4MSAwIDAgMC01LjM3Ni01LjM4NiA1LjM4MSA1LjM4MSAwIDAgMC01LjM3NiA1LjM4NnY1LjM4N2g1LjM3NmE1LjM4MSA1LjM4MSAwIDAgMCA1LjM3Ni01LjM4N20tMTQuMzM2IDBWNS41MkE1LjM4MSA1LjM4MSAwIDAgMCAzNC4wNDguMTMzYTUuMzgxIDUuMzgxIDAgMCAwLTUuMzc2IDUuMzg3djE0LjM2NGE1LjM4MSA1LjM4MSAwIDAgMCA1LjM3NiA1LjM4NyA1LjM4MSA1LjM4MSAwIDAgMCA1LjM3Ni01LjM4NyIgZmlsbD0iIzJFQjY3RCIvPjxwYXRoIGQ9Ik0zNC4wNDggNTRhNS4zODEgNS4zODEgMCAwIDAgNS4zNzYtNS4zODcgNS4zODEgNS4zODEgMCAwIDAtNS4zNzYtNS4zODZoLTUuMzc2djUuMzg2QTUuMzgxIDUuMzgxIDAgMCAwIDM0LjA0OCA1NG0wLTE0LjM2NWgxNC4zMzZhNS4zODEgNS4zODEgMCAwIDAgNS4zNzYtNS4zODYgNS4zODEgNS4zODEgMCAwIDAtNS4zNzYtNS4zODdIMzQuMDQ4YTUuMzgxIDUuMzgxIDAgMCAwLTUuMzc2IDUuMzg3IDUuMzgxIDUuMzgxIDAgMCAwIDUuMzc2IDUuMzg2IiBmaWxsPSIjRUNCMjJFIi8+PHBhdGggZD0iTTAgMzQuMjQ5YTUuMzgxIDUuMzgxIDAgMCAwIDUuMzc2IDUuMzg2IDUuMzgxIDUuMzgxIDAgMCAwIDUuMzc2LTUuMzg2di01LjM4N0g1LjM3NkE1LjM4MSA1LjM4MSAwIDAgMCAwIDM0LjI1bTE0LjMzNi0uMDAxdjE0LjM2NEE1LjM4MSA1LjM4MSAwIDAgMCAxOS43MTIgNTRhNS4zODEgNS4zODEgMCAwIDAgNS4zNzYtNS4zODdWMzQuMjVhNS4zODEgNS4zODEgMCAwIDAtNS4zNzYtNS4zODcgNS4zODEgNS4zODEgMCAwIDAtNS4zNzYgNS4zODciIGZpbGw9IiNFMDFFNUEiLz48L2c+PC9zdmc+&labelColor=611f69)](https://kotlinlang.slack.com/messages/graphql-kotlin/)

GraphQL Kotlin is a collection of libraries, built on top of [graphql-java](https://www.graphql-java.com/), that simplify running GraphQL clients and servers in Kotlin.

Visit our [documentation site](https://expediagroup.github.io/graphql-kotlin) for more details.

## 📦 Modules

* [clients](/clients) - Lightweight GraphQL Kotlin HTTP clients based on Ktor HTTP client and Spring WebClient
* [examples](/examples) - Example apps that use graphql-kotlin libraries to test and demonstrate usages
* [executions](/executions) - Custom instrumentations for a GraphQL operation
* [generator](/generator) - Code-First schema generator and extensions to build Apollo Federation schemas
* [plugins](/plugins) - Gradle and Maven plugins
* [servers](/servers) - Common and library specific modules for running a GraphQL server

## ⌨️ Usage

While all the individual modules of `graphql-kotlin` are published as stand-alone libraries, the most common use cases are running a server and generating a type-safe client.

### Server Example

A basic example of how you can run a GraphQL server can be found on our [server documentation section](https://expediagroup.github.io/graphql-kotlin/docs/server/graphql-server).

### Client Example

A basic setup of a GraphQL client can be found on our [client documentation section](https://expediagroup.github.io/graphql-kotlin/docs/client/client-overview).

## 📋 Documentation

More examples and documentation are available on our [documentation site](https://expediagroup.github.io/graphql-kotlin) hosted in GitHub Pages.
We also have the [examples](/examples) module which can be run locally for testing and shows example code using the libraries.

If you have a question about something you can not find in our documentation, the indivdual module `README`s, or [javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-schema-generator), feel free to contribute to the docs or [start a disucssion](https://github.com/ExpediaGroup/graphql-kotlin/discussions) and tag it with the question label.

If you would like to contribute to our documentation see the [website](/website) directory for more information.

## 🗞 Blog Posts and Videos

The [Blogs & Videos page](https://expediagroup.github.io/graphql-kotlin/docs/blogs-and-videos) in the GraphQL Kotlin documentation links to blog posts, release announcements, conference talks about the library, and general talks about GraphQL at Expedia Group.

## 👥 Contact

This project is part of Expedia Group Open Source but also maintained by a dedicated team

* Expedia Group OSS
  * https://expediagroup.github.io

* GraphQL Kotlin Committers
  * Github team: `@ExpediaGroup/graphql-kotlin-committers`

If you have a specific question about the library or code, please [start a discussion](https://github.com/ExpediaGroup/graphql-kotlin/discussions) for the community.

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
