---
id: spring-beans
title: Automatically Created Beans
---
`graphql-kotlin-spring-server` automatically creates all the necessary beans to start a GraphQL server.
Many of the beans are conditionally created and the default behavior can be customized by providing custom overriding beans in your application context.

## Execution

| Bean                             | Description                                                                                                                                                                                         |
|:---------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| DataFetcherExceptionHandler      | GraphQL exception handler used from the various execution strategies, defaults to [SimpleDataFetcherExceptionHandler](https://www.graphql-java.com/documentation/v16/execution/) from graphql-java. |
| KotlinDataFetcherFactoryProvider | Factory used during schema construction to obtain `DataFetcherFactory` that should be used for target function (using Spring aware `SpringDataFetcher`) and property resolution.                    |
| KotlinDataLoader (optional)      | Any number of beans created that implement `KotlinDataLoader`. See [Data Loaders](../data-loader/data-loader.md) for more details.                                                                  |
| KotlinDataLoaderRegistryFactory  | A factory class that creates a `KotlinDataLoaderRegistry` of all the `KotlinDataLoaders`. Defaults to empty registry.                                                                               |

## Non-Federated Schema

:::note

_Created only if federation is **disabled**_

:::

| Bean                  | Description                                                                                                                                                                                                                                                                                                                                        |
|:----------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| GraphQLSchema         | GraphQL schema generated based on the schema generator configuration and  `Query`, `Mutation` and `Subscription` objects available in the application context.                                                                                                                                                                                     |
| SchemaGeneratorConfig | Schema generator configuration information, see [Schema Generator Configuration](../../schema-generator/customizing-schemas/generator-config.md) for details. Can be customized by providing `TopLevelNames`, [SchemaGeneratorHooks](../../schema-generator/customizing-schemas/generator-config.md) and `KotlinDataFetcherFactoryProvider` beans. |
| GraphQLTypeResolver   | GraphQL type resolver that is used to lookup polymorphic type hierarchy. Defaults to use `ClassGraph` to load polymorphic information directly from classpath.<br/><br/>**NOTE: When generating GraalVM Native Images you cannot use classpath scanning and have to explicitly provide this information.**                                         |

## Federated Schema

:::note

_Created only if federation is **enabled**_

:::

| Bean                            | Description                                                                                                                                                                                                                                                                                                                  |
|:--------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| FederatedGraphQLTypeResolver    | GraphQL type resolver that is used to lookup polymorphic type hierarchy and locate federated entities. Defaults to use `ClassGraph` to load information directly from classpath.<br/><br/>**NOTE: When generating GraalVM Native Images you cannot use classpath scanning and have to explicitly provide this information.** |
| FederatedTypeResolvers          | List of `FederatedTypeResolvers` marked as beans that should be added to hooks. See [Federated Type Resolution](../../schema-generator/federation/type-resolution.md) for more details                                                                                                                                       |
| FederatedSchemaGeneratorHooks   | Schema generator hooks used to build federated schema                                                                                                                                                                                                                                                                        |
| FederatedSchemaGeneratorConfig  | Federated schema generator configuration information. You can customize the configuration by providing `TopLevelNames`, `FederatedSchemaGeneratorHooks` and `KotlinDataFetcherFactoryProvider` beans                                                                                                                         |
| FederatedTracingInstrumentation | If `graphql.federation.tracing.enabled` is true, it adds tracing info to the response via the [apollo federation-jvm](https://github.com/apollographql/federation-jvm) library.                                                                                                                                              |
| GraphQLSchema                   | GraphQL schema generated based on the federated schema generator configuration and  `Query`, `Mutation` and `Subscription` objects available in the application context.                                                                                                                                                     |

## GraphQL Configuration

| Bean                                  | Description                                                                                                                                                                                                                                 |
|:--------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Instrumentation (optional)            | Any number of beans created that implement `graphql-java` [Instrumentation](https://www.graphql-java.com/documentation/v16/instrumentation/) will be pulled in. The beans can be ordered by implementing the Spring `Ordered` interface.    |
| ExecutionIdProvider (optional)        | Any number of beans created that implement `graphql-java` [ExecutionIdProvider](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/ExecutionIdProvider.java) will be pulled in.                       |
| PreparsedDocumentProvider (optional)  | Any number of beans created that implement `graphql-java` [PreparsedDocumentProvider](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/preparsed/PreparsedDocumentProvider.java) will be pulled in. |
| GraphQL                               | GraphQL execution object generated using `GraphQLSchema` with default async execution strategies. The GraphQL object can be customized by optionally providing the above beans in the application context.                                  |
| SpringGraphQLRequestParser            | Provides the Spring specific logic for parsing the HTTP request into a common GraphQLRequest. See [GraphQLRequestParser](../graphql-request-parser.md)                                                                                      |
| SpringGraphQLContextFactory           | Spring specific factory that uses the `ServerRequest`. The `GraphQLContext` generated can be any object. See [GraphQLContextFactory](../graphql-context-factory.md).                                                                        |
| GraphQLRequestHandler                 | Handler invoked from `GraphQLServer` that executes the incoming request, defaults to [GraphQLRequestHandler](../graphql-request-handler.md).                                                                                                |
| SpringGraphQLServer                   | Spring specific object that takes in a `ServerRequest` and returns a `GraphQLResponse` using all the above implementations. See [GraphQLServer](../graphql-server.md)                                                                       |
| IDValueUnboxer                        | Value unboxer that provides support for handling ID value class                                                                                                                                                                             |

## Subscriptions

:::note

_Created only if the `Subscription` marker interface is used_

:::

| Bean                                    | Description                                                                                                                                                                                                                |
|:----------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| FlowSubscriptionSchemaGeneratorHooks | Schema generator hooks that provide support for using `Flow` in your subscriptions |
| WebSocketHandlerAdapter                 | Spring class for handling web socket http requests. See [Spring documentation](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/socket/server/support/WebSocketHandlerAdapter.html) |
| HandlerMapping | Maps websocket URL to the corresponding web socket handler |

### graphql-transport-ws

| Bean                                    | Description                                                                       |
|:----------------------------------------|:----------------------------------------------------------------------------------|
| SpringSubscriptionGraphQLContextFactory | Generates GraphQL subscription context based on the WebSocket session information |
| SpringGraphQLSubscriptionRequestParser  | Parses incoming WebSocket messages                                                |
| SpringGraphQLSubscriptionHooks          | Provides hooks into the subscription request lifecycle                            |
| SubscriptionWebSocketHandler            | WebSocketHandler that implements the `graphql-transport-ws` subscription protocol |

### (deprecated) subscription-transport-ws

| Bean                                    | Description                                                                                                           |
|:----------------------------------------|:----------------------------------------------------------------------------------------------------------------------|
| ApolloSubscriptionHooks                 | Provides hooks into the subscription request lifecycle. See [the subscription docs](spring-subscriptions.md)          |
| SpringSubscriptionGraphQLContextFactory | Spring specific factory that uses the `WebSocketSession`. See [GraphQLContextFactory](../graphql-context-factory.md). |
| ApolloSubscriptionProtocolHandler       | Implementation of the `subscription-transport-ws` subscription protocol                                               |
| ApolloSubscriptionWebSocketHandler      | WebSocketHandler that delegates handling of the messages to the `ApolloSubscriptionProtocolHandler` bean              |

## Fixed Beans

The following beans cannot be overridden, but may have options to disable them:

-   Route handler for GraphQL queries and mutations endpoint.
-   Route handler for the SDL endpoint. Created only if sdl route is enabled.
-   Route handler for [GraphQL graphiql browser IDE](https://github.com/graphql/graphiql). Created only if graphiql is enabled.
-   Route handler for the subscriptions endpoint. Created only if subscriptions are used.
-   `ApolloSubscriptionProtocolHandler` for handling GraphQL subscriptions using the [`graphql-ws` protocol](https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md). Created only if subscriptions are used.
-   `SubscriptionWebSocketHandler` that utilizes above subscription protocol handler. Created only if subscriptions are used.
