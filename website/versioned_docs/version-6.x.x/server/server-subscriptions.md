---
id: server-subscriptions
title: Subscriptions
---
If you are using one of the official server implementations for GraphQL Kotlin, it will have subscription handling setup for you.

-   See `graphql-kotlin-spring-server` [subscriptions](spring-server/spring-subscriptions.md)

Subscriptions require a more in-depth knowledge of how the specific server library handles protocols and streaming.
Since we can only support `Publisher` from `graphql-java` in this common library, we can not provide any common logic for subscriptions.
Therefore, you will still need to implement the route and request handling for subscriptions separately if you are not using a provided server implementation.
