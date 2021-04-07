# GraphQL Kotlin Spring Server
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-spring-server.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-spring-server%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-spring-server.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-spring-server)

`graphql-kotlin-spring-server` is a Spring Boot autoconfiguration library that automatically configures beans required to start up reactive GraphQL web server.


## Installation

Using a JVM dependency manager, link `graphql-kotlin-spring-server` to your project.

With Maven:

```xml
<dependency>
  <groupId>com.expediagroup</groupId>
  <artifactId>graphql-kotlin-spring-server</artifactId>
  <version>${latestVersion}</version>
</dependency>
```

With Gradle:

```kotlin
implementation("com.expediagroup", "graphql-kotlin-spring-server", latestVersion)
```

## Usage

At a minimum, in order for `graphql-kotlin-spring-server` to automatically configure your GraphQL web server you need to specify list of supported packages that can be scan for exposing your schema objects through reflections.

```yaml
graphql:
  packages:
    - "com.your.package"
```

In order to expose your queries, mutations and subscriptions in the GraphQL schema you need to implement corresponding marker interfaces and they will be automatically picked up by `graphql-kotlin-spring-server` autoconfiguration library.

```kotlin
@Component
class MyAwesomeQuery : Query {
  fun myAwesomeQuery(): Widget { ... }
}

@Component
class MyAwesomeMutation : Mutation {
  fun myAwesomeMutation(widget: Widget): Widget { ... }
}

data class Widget(val id: Int, val value: String)
```

will result in a Spring Boot reactive GraphQL web application with following schema.

```graphql
schema {
  query: Query
  mutation: Mutation
}

type Query {
  myAwesomeQuery(): Widget!
}

type Mutation {
  myAwesomeMutation(widget: Widget!): Widget!
}

type Widget {
  id: Int!
  value: String!
}
```

## Documentation

There are more examples and documentation in our
[documentation](https://expediagroup.github.io/graphql-kotlin), or you can view the [javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-spring-server) for all published versions.

If you have a question about something you can not find in our documentation or javadocs, feel free to [create an issue](https://github.com/ExpediaGroup/graphql-kotlin/issues) and tag it with the question label.
