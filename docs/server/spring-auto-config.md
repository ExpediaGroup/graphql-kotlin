---
id: spring-auto-config
title: Spring Boot Auto Configuration
---

[graphql-kotlin-spring-server](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/graphql-kotlin-spring-server)
is a Spring Boot auto-configuration library that automatically configures beans required to start up reactive GraphQL
web server.

At a minimum, in order for `graphql-kotlin-spring-server` to automatically configure your GraphQL web server you need to
specify list of supported packages that can be scanned for exposing your schema objects through reflections.

You can do this through the spring application config or by overriding the `SchemaGeneratorConfig` bean. See customization below.

```yaml
graphql:
  packages:
    - "com.your.package"
```


In order to expose your queries, mutations and/or subscriptions in the GraphQL schema you simply need to implement
corresponding marker interface and they will be automatically picked up by `graphql-kotlin-spring-server`
auto-configuration library.

```kotlin
class MyAwesomeQuery : Query {
  fun myAwesomeQuery(): Widget { ... }
}

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
