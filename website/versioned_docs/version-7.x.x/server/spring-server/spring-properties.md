---
id: spring-properties
title: Configuration Properties
---

`graphql-kotlin-spring-server` relies
on [GraphQLConfigurationProperties](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/servers/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/server/spring/GraphQLConfigurationProperties.kt)
to provide various customizations of the auto-configuration library. All applicable configuration properties
expose [configuration
metadata](https://docs.spring.io/spring-boot/docs/current/reference/html/configuration-metadata.html) that provide
details on the supported configuration properties.

| Property                                    | Description                                                                                                                              | Default Value                 |
|---------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------|
| graphql.endpoint                            | GraphQL server endpoint                                                                                                                  | graphql                       |
| graphql.packages                            | List of supported packages that can contain GraphQL schema type definitions                                                              |                               |
| graphql.printSchema                         | Boolean flag indicating whether to print the schema after generator creates it                                                           | false                         |
| graphql.serializationLibrary                | Configure which serialization library will be used for GraphQLRequest and GraphQLResponse types, supported libraries: JACKSON / FASTJSON | JACKSON                       |
| graphql.federation.enabled                  | Boolean flag indicating whether to generate federated GraphQL model                                                                      | false                         |
| graphql.federation.optInV2                  | Boolean flag indicating whether to generate Federation v2 GraphQL model                                                                  | false                         |
| graphql.federation.tracing.enabled          | Boolean flag indicating whether add federated tracing data to the extensions                                                             | true (if federation enabled)  |
| graphql.federation.tracing.debug            | Boolean flag to log debug info in the federated tracing                                                                                  | false (if federation enabled) |
| graphql.introspection.enabled               | Boolean flag indicating whether introspection queries are enabled                                                                        | true                          |
| graphql.playground.enabled                  | Boolean flag indicating whether to enable Prisma Labs Playground GraphQL IDE                                                             | false                         |
| graphql.playground.endpoint                 | Prisma Labs Playground GraphQL IDE endpoint                                                                                              | playground                    |
| graphql.graphiql.enabled                    | Boolean flag indicating whether to enable GraphiQL GraphQL IDE                                                                           | true                          |
| graphql.graphiql.endpoint                   | Prisma Labs Playground GraphQL IDE endpoint                                                                                              | graphiql                      |
| graphql.sdl.enabled                         | Boolean flag indicating whether to expose SDL endpoint                                                                                   | true                          |
| graphql.sdl.endpoint                        | GraphQL SDL endpoint                                                                                                                     | sdl                           |
| graphql.subscriptions.connectionInitTimeout | Server timeout (in milliseconds) between establishing web socket connection and receiving connection-init message                        | 60_000                        |
| graphql.subscriptions.endpoint              | GraphQL subscriptions endpoint                                                                                                           | subscriptions                 |
| graphql.subscriptions.keepAliveInterval     | **Deprecated**. Keep the websocket alive and send a message to the client every interval in ms. Defaults to not sending messages         | null                          |
| graphql.subscriptions.protocol              | WebSocket based subscription protocol. Supported protocols: APOLLO_SUBSCRIPTIONS_WS / GRAPHQL_WS                                         | GRAPHQL_WS                    |
| graphql.batching.enabled                    | Boolean flag indicating whether to enable custom dataloader instrumentations for 1 or more GraphQL Operations                            | false                         |
| graphql.batching.strategy                   | Configure which custom dataloader instrumentation will be used (LEVEL_DISPATCHED or SYNC_EXHAUSTION)                                     | LEVEL_DISPATCHED              |
