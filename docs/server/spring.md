---
id: spring
title: Spring
---
[graphql-kotlin-spring-server](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/graphql-kotlin-spring-server)
is a Spring Boot auto-configuration library that automatically configures beans required to start up reactive GraphQL
web server.

At a minimum, in order for `graphql-kotlin-spring-server` to automatically configure your GraphQL web server you need to
specify list of supported packages that can be scanned for exposing your schema objects through reflections.

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

## Customization

All beans created by `graphql-kotlin-spring-server` are conditionally created. If any of the target beans are created in
the application context, auto-configuration will back off.

Conditionally generated beans:

* **SchemaGeneratorConfig** - schema generation configuration information, see
  [Spring Configuration](spring-config) for details. _You should
  register custom configuration bean if you want to specify custom schema generator hooks._
* **FederatedTypeRegistry** - default type registry without any resolvers, created only if generating federated GraphQL
  schema. _You should register your custom type registry bean whenever implementing federated GraphQL schema with
  extended types_.
* **GraphQLSchema** - GraphQL schema generated based on the schema generator configuration and  `Query`, `Mutation` and
  `Subscription` objects available in the application context
* **DataFetcherExceptionHandler** - GraphQL exception handler used from the various execution strategies, defaults to
  [KotlinDataFetcherExceptionHandler](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/exception/KotlinDataFetcherExceptionHandler.kt)
  
* **GraphQL** - `graphql-java` GraphQL query execution engine generated using `GraphQLSchema` with default async
  execution strategies. GraphQL engine can be customized by optionally providing
  [Instrumentation](https://www.graphql-java.com/documentation/v13/instrumentation/),
  [ExecutionIdProvider](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/ExecutionIdProvider.java)
  and
  [PreparsedDocumentProvider](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/preparsed/PreparsedDocumentProvider.java)
  in the application context.
* **QueryHandler** - handler invoked from GraphQL query route that executes the incoming request, defaults to
  [SimpleQueryHandler](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/QueryHandler.kt)
* **GraphQLContextFactory** - factory used by context WebFilter to generate GraphQL context based on the incoming
  request. GraphQL context can be any object. Defaults to empty
  [GraphQLContext](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/GraphQLContext.java)
* **SubscriptionHandler** - Web socket handler for executing GraphQL subscriptions, defaults to
  [SimpleSubscriptionHandler](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/SubscriptionHandler.kt#L49),
  created only if `Subscription` bean is available in the context
* **WebSocketHandlerAdapter** - [see Spring
  documentation](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/socket/server/support/WebSocketHandlerAdapter.html),
  created only if `Subscription` bean is available in the context

The following beans are currently automatically created and cannot be disabled:

* Web filter for generating and populating GraphQL context
* Default routes for GraphQL queries/mutations and SDL endpoint
* Default subscription handler mapping, created only if `Subscription` bean is available in the context
