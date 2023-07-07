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

`graphql-kotlin-ktor-server` provides support for Kotlin `Flow` by automatically configuring schema generation process with `FlowSubscriptionSchemaGeneratorHooks`
and GraphQL execution with `FlowSubscriptionExecutionStrategy`.

:::info
If you define your subscriptions using Kotlin `Flow`, make sure to extend `FlowSubscriptionSchemaGeneratorHooks` whenever you need to provide some custom hooks.
:::

## Subscription Protocols

### `graphql-transport-ws` subprotocol

We have implemented subscriptions in Ktor WebSockets following the [`graphql-transport-ws`](https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md) sub-protocol
from [The Guild](https://the-guild.dev/). This requires that your client send and parse messages in a specific format.
See protocol documentation for expected messages.

```kotlin
install(Routing) {
    graphQLSubscriptionsRoute()
}
```

## Subscription Execution Hooks

Subscription execution hooks allow you to "hook-in" to the various stages of the connection lifecycle and execute custom logic based on the event. By default, all subscription execution hooks are no-op.
If you would like to provide some custom hooks, you can do so by providing your own implementation of `KtorGraphQLSubscriptionHooks`.

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
