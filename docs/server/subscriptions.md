---
id: subscriptions
title: Subscriptions
---

## Schema
To see more details of how to implement subscriptions in your schema, see [executing subscriptions](../execution/subscriptions).

## `graphql-ws` subprotocol
We have implemented subscriptions in Spring WebSockets following the [`graphql-ws` subprotocol](https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md) defined by Apollo. This requires that your client send and parse messages in a specific format.

You can see more details in the file [ApolloSubscriptionProtocolHandler](https://github.com/ExpediaGroup/graphql-kotlin/blob/9d0ace47b2d1620e16ed46ca4b89769ea83446eb/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ApolloSubscriptionProtocolHandler.kt).

If you would like to implement subscription handler, you can provide a primaary spring bean for `HandlerMapping` that overrides the [default one](https://github.com/ExpediaGroup/graphql-kotlin/blob/9d0ace47b2d1620e16ed46ca4b89769ea83446eb/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/SubscriptionAutoConfiguration.kt#L68) which sets the url for subscription to the Apollo subscription handler.

## Example
You can see an example implementation of a `Subscription` in the [example app](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/sample/subscriptions/SimpleSubscription.kt).




