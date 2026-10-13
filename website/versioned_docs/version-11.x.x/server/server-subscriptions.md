---
id: server-subscriptions
title: Subscriptions
---

GraphQL Kotlin provides `WebSocket` subscription support with the generic `Flow` based implementation of [`GraphQL WS` subscription
protocol](https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md). Server implementations should extend generic abstract `GraphQLWebSocketServer<Session, Message>` class and fill server
specific details on how to read incoming messages from the WebSocket session as well as how to send responses back to the client.

If you are using one of the official server implementations for GraphQL Kotlin, it will have subscription handling setup for you.

-   See `graphql-kotlin-spring-server` [subscriptions](spring-server/spring-subscriptions.md)
-   See `graphql-kotlin-ktor-server` [subscriptions](ktor-server/ktor-subscriptions.md)
