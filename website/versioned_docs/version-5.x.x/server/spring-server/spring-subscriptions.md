---
id: spring-subscriptions
title: Subscriptions
---
_To see more details on how to implement subscriptions in your schema, see the schema generator docs on [executing subscriptions](../../schema-generator/execution/subscriptions.md).
This page lists the `graphql-kotlin-spring-server` specific features._

## Flow Support

`graphql-kotlin-spring-server` provides automatic support for Kotlin `Flow` through `FlowSubscriptionExecutionStrategy`
that supports existing `Publisher`s and relies on Kotlin reactive-streams interop to convert `Flow` to a `Publisher`.

## `graphql-ws` subprotocol

We have implemented subscriptions in Spring WebSockets following the [`graphql-ws` subprotocol](https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md) defined by Apollo.
This requires that your client send and parse messages in a specific format.

If you would like to implement your own subscription handler, you can provide a primary spring bean for `HandlerMapping` that overrides the [default one](./spring-beans.md) which sets the url for subscriptions to the Apollo subscription handler.

## Subscription Hooks

In line with the Apollo protocol, we have implemented hooks to execute functions at different stages of the connection lifecycle.
If you would like to implement your own subscription hooks, you can provide a primary spring bean for `ApolloSubscriptionHooks` that overrides the [default one](./spring-beans.md) which do not perform any actions.

### `onConnect`
Allows validation of connectionParams prior to starting the connection.
You can reject the connection by throwing an exception.
If you need to forward state to execution, update and return the [GraphQLContext](./spring-graphql-context.md).

### `onOperation`
Called when the client executes a GraphQL operation. The context can not be updated here, it is read only.

### `onOperationComplete`
Called when client's unsubscribes

### `onDisconnect`
Called when the client disconnects

## Example

You can see an example implementation of a `Subscription` in the [example app](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/server/spring-server/src/main/kotlin/com/expediagroup/graphql/examples/server/spring/subscriptions/SimpleSubscription.kt).
