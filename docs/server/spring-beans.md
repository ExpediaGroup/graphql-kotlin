---
id: spring-beans
title: Automatically Created Beans
---

`graphql-kotlin-spring-server` automatically creates all the necessary beans to start GraphQL web server. Many of the beans are conditionally created and default behavior
can be customized by providing custom beans in your application context. See sections below for the information about all automatically created beans.

## Schema Generation

| Bean                           | Description |
|:-------------------------------|:------------|
| SchemaGeneratorConfig          | Schema generator configuration information, see [Schema Generator Configuration](https://expediagroup.github.io/graphql-kotlin/docs/writing-schemas/generator-config) for details. Can be customized by providing `TopLevelNames`, [SchemaGeneratorHooks](https://expediagroup.github.io/graphql-kotlin/docs/writing-schemas/generator-config#schema-generator-hooks) and `KotlinDataFetcherFactoryProvider` beans. |
| FederatedTypeRegistry          | Default type registry without any resolvers. See [Federated Type Resolution](https://expediagroup.github.io/graphql-kotlin/docs/federated/type-resolution) for more details.<br><br>_Created only if federation is enabled. You should register your custom type registry bean whenever implementing federated GraphQL schema with extended types_. |
| FederatedSchemaGeneratorHooks  | Schema generator hooks used to build federated schema.<br><br>_Created only if federation is enabled_. |
| FederatedSchemaGeneratorConfig | Federated schema generator configuration information. You can customize the configuration by providing `TopLevelNames`, `FederatedSchemaGeneratorHooks` and `KotlinDataFetcherFactoryProvider` beans.<br><br>_Created instead of default `SchemaGeneratorConfig` if federation is enabled_. |
| GraphQLSchema                  | GraphQL schema generated based on the schema generator configuration and  `Query`, `Mutation` and `Subscription` objects available in the application context. |

## Execution

| Bean                           | Description |
|:-------------------------------|:------------|
| ContextWebFilter               | Default web filter that populates GraphQL context in the reactor subscriber context. |
| DataFetcherExceptionHandler    | GraphQL exception handler used from the various execution strategies, defaults to [KotlinDataFetcherExceptionHandler](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/exception/KotlinDataFetcherExceptionHandler.kt). |
| GraphQL                        | GraphQL query execution engine generated using `GraphQLSchema` with default async execution strategies. GraphQL engine can be customized by optionally providing a list of [Instrumentation](https://www.graphql-java.com/documentation/v13/instrumentation/) beans (which can be ordered by implementing Spring Ordered interface), [ExecutionIdProvider](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/ExecutionIdProvider.java) and [PreparsedDocumentProvider](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/preparsed/PreparsedDocumentProvider.java) in the application context. |
| DataLoaderRegistryFactory      | Factory used to create DataLoaderRegistry instance per query execution. See [graphql-java documentation](https://www.graphql-java.com/documentation/v13/batching/) for more details. |
| QueryHandler                   | Handler invoked from GraphQL query route that executes the incoming request, defaults to [SimpleQueryHandler](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/QueryHandler.kt). |
| GraphQLContextFactory          | Factory used by context WebFilter to generate GraphQL context based on the incoming request. GraphQL context can be any object. Defaults to empty [GraphQLContext](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/GraphQLContext.java). |
| SubscriptionHandler            | Web socket handler for executing GraphQL subscriptions, defaults to [SimpleSubscriptionHandler](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/SubscriptionHandler.kt#L49).<br><br>_Created only if `Subscription` bean is available in the context._ |
| WebSocketHandlerAdapter        | See [Spring documentation](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/socket/server/support/WebSocketHandlerAdapter.html).<br><br>_Created only if `Subscription` bean is available in the context._ |

The following beans are currently automatically created and cannot be disabled:

* Default routes for GraphQL queries/mutations and SDL endpoint
* Default route for [Prisma Labs Playground](https://github.com/prisma-labs/graphql-playground), created only if playground is enabled
* Default `ApolloSubscriptionProtocolHandler` for handling GraphQL subscriptions, created only if `Subscription` bean is available in the context
* Default `SubscriptionWebSocketHandler` that utilizes above subscription protocol handler, created only if `Subscription` bean is available in the context
* Default subscription handler mapping, created only if `Subscription` bean is available in the context
