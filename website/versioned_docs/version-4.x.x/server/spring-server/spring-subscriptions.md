---
id: spring-subscriptions
title: Subscriptions
original_id: spring-subscriptions
---
_To see more details on how to implement subscriptions in your schema, see the schema generator docs on [executing subscriptions](../../schema-generator/execution/subscriptions.md).
This page lists the `graphql-kotlin-spring-server` specific features._

## Flow Support

`graphql-kotlin-spring-server` provides automatic support for Kotlin `Flow` through `FlowSubscriptionExecutionStrategy`
that supports existing `Publisher`s and relies on Kotlin reactive-streams interop to convert `Flow` to a `Publisher`.

## `graphql-ws` subprotocol

We have implemented subscriptions in Spring WebSockets following the [`graphql-ws` subprotocol](https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md) defined by Apollo.
This requires that your client send and parse messages in a specific format.

You can see more details in the file [ApolloSubscriptionProtocolHandler](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ApolloSubscriptionProtocolHandler.kt).

If you would like to implement your own subscription handler, you can provide a primary spring bean for `HandlerMapping` that overrides the [default one](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/SubscriptionAutoConfiguration.kt) which sets the url for subscriptions to the Apollo subscription handler.

### Subscription Hooks

In line with the Apollo protocol, we have implemented hooks to execute functions at different stages of the connection lifecycle:

-   onConnect
-   onOperation
-   onOperationComplete
-   onDisconnect

You can see more details in the file [ApolloSubscriptionHooks](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ApolloSubscriptionHooks.kt).

If you would like to implement your own subscription hooks, you can provide a primary spring bean for `ApolloSubscriptionHooks` that overrides the [default one](./spring-beans.md) which do not perform any actions.

## Example

You can see an example implementation of a `Subscription` in the [example app](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/subscriptions/SimpleSubscription.kt).
