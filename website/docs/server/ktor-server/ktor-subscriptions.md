---
id: ktor-subscriptions
title: Subscriptions
---
_To see more details on how to implement subscriptions in your schema, see the schema generator docs on [executing subscriptions](../../schema-generator/execution/subscriptions.md).
This page lists the `graphql-kotlin-ktor-server` specific features._

## Prerequisites

To start using Subscriptions, you may need install [WebSockets](https://ktor.io/docs/websocket.html) plugin to your Ktor server config.
```kotlin
install(WebSockets)
```
See [plugin docs](https://ktor.io/docs/websocket.html#configure) to get more info about the `WebSocketOptions` configuration.

## Flow Support

`graphql-kotlin-ktor-server` provides support for Kotlin `Flow` by automatically configuring `FlowSubscriptionSchemaGeneratorHooks`
and `FlowSubscriptionExecutionStrategy` beans.

:::info
If you define your subscriptions using Kotlin `Flow`, make sure to extend `FlowSubscriptionSchemaGeneratorHooks` whenever you need to provide some custom hooks.
:::

## WebSocket Sub-protocols

We have implemented subscriptions in Ktor WebSockets following the [`graphql-transport-ws`](https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md) sub-protocol.
This one is enabled by default if you don't override the config:

```kotlin
install(Routing) {
    graphQLSubscriptionsRoute()
}
```

If you would like to implement your own subscription handler, e.g. to support another sub-protocol, you can provide your implementation to the `graphQLSubscriptionsRoute`
as shown below:

```kotlin
install(Routing) {
    graphQLSubscriptionsRoute(handlerOverride = MyOwnSubscriptionsHandler())
}
```

## Subscription Hooks

In line with the Apollo protocol, we have implemented hooks to execute functions at different stages of the connection lifecycle.
If you would like to implement your own subscription hooks, you can provide your own implementation of `KtorGraphQLSubscriptionHooks`. The default implementation does not perform any actions.

### `onConnect`
Allows validation of connectionParams prior to starting the connection.
You can reject the connection by throwing an exception.
A `GraphQLContext` returned from this hook will be later passed to subsequent hooks.

### `onOperation`
Called when the client executes a GraphQL operation.

### `onOperationComplete`
Called when client's unsubscribes

### `onDisconnect`
Called when the client disconnects

## Example

You can see an example implementation of a `Subscription` in the [example app](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/server/ktor-server/src/main/kotlin/com/expediagroup/graphql/examples/server/ktor/schema/ExampleSubscriptionService.kt).
