---
id: spring-properties
title: Configuration Properties
---

`graphql-kotlin-spring-server` relies on [GraphQLConfigurationProperties](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/GraphQLConfigurationProperties.kt) 
to provide various customizations of the auto-configuration library. All applicable configuration properties expose [configuration
metadata](https://docs.spring.io/spring-boot/docs/current/reference/html/configuration-metadata.html) that provides
details on the supported configuration properties.

| Property | Description | Default Value |
|----------|-------------|---------------|
| graphql.endpoint | GraphQL server endpoint | "graphql" |
| graphql.packages | List of supported packages that can contain GraphQL schema type definitions | |
| graphql.federation.enabled | Boolean flag indicating whether to generate federated GraphQL model | |
| graphql.subscriptions.endpoint | GraphQL subscriptions endpoint | "subscriptions" |
| graphql.subscriptions.keepAliveInterval | Keep the websocket alive and send a message to the client every interval in ms. Defaults to not sending messages | null |
| graphql.playground.enabled | Boolean flag indicating whether to enabled Prisma Labs Playground GraphQL IDE | |
| graphql.playground.endpoint | Prisma Labs Playground GraphQL IDE endpoint | "playground" |
