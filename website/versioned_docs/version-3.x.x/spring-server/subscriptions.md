---
id: subscriptions
title: Subscriptions
original_id: subscriptions
---

## Schema
To see more details of how to implement subscriptions in your schema, see [executing subscriptions](../schema-generator/execution/subscriptions.md).

## `graphql-ws` subprotocol
### Overview
We have implemented subscriptions in Spring WebSockets following the [`graphql-ws` subprotocol](https://github.com/apollographql/subscriptions-transport-ws/blob/3.x.x/PROTOCOL.md) defined by Apollo. This requires that your client send and parse messages in a specific format.

You can see more details in the file [ApolloSubscriptionProtocolHandler](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ApolloSubscriptionProtocolHandler.kt).

If you would like to implement your own subscription handler, you can provide a primary spring bean for `HandlerMapping` that overrides the [default one](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/SubscriptionAutoConfiguration.kt) which sets the url for subscriptions to the Apollo subscription handler.

### Subscription Hooks
In line with the protocol, we have implemented hooks to execute functions at different stages of the connection lifecycle:
- onConnect
- onOperation
- onOperationComplete
- onDisconnect

You can see more details in the file [ApolloSubscriptionHooks](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ApolloSubscriptionHooks.kt).

If you would like to implement your own subscription hooks, you can provide a primary spring bean for `ApolloSubscriptionHooks` that overrides the [default one](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/SubscriptionAutoConfiguration.kt) which do not perform any actions.


## Example
You can see an example implementation of a `Subscription` in the [example app](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/subscriptions/SimpleSubscription.kt).



