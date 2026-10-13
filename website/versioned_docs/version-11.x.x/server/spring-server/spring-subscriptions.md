---
id: spring-subscriptions
title: Subscriptions
---
_To see more details on how to implement subscriptions in your schema, see the schema generator docs on [executing subscriptions](../../schema-generator/execution/subscriptions.md).
This page lists the `graphql-kotlin-spring-server` specific features._

## Flow Support

`graphql-kotlin-spring-server` provides automatic support for Kotlin `Flow` by automatically configuring `FlowSubscriptionSchemaGeneratorHooks`
and `FlowSubscriptionExecutionStrategy` beans.

:::info
If you define your subscriptions using Kotlin `Flow`, make sure to extend `FlowSubscriptionSchemaGeneratorHooks` whenever you need to provide some custom hooks.
:::

## Subscription Protocols

### `graphql-transport-ws` subprotocol

We have implemented subscriptions in Spring WebSockets following the [`graphql-transport-ws` subprotocol](https://github.com/enisdenjo/graphql-ws)
from [The Guild](https://the-guild.dev/). This requires that your client send and parse messages in a specific format.
See protocol documentation for expected messages.

`graphql-transport-ws` is the default subscription protocol that is enabled by default.

### (deprecated) `subscriptions-transport-ws` subprotocol

:::caution
`subscriptions-transport-ws` was deprecated in favor of [`graphql-transport-ws` protocol](https://github.com/enisdenjo/graphql-ws).
:::

We have implemented subscriptions in Spring WebSockets following the [`subscriptions-transport-ws` subprotocol](https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md)
defined by [Apollo](https://www.apollographql.com/). This requires that your client send and parse messages in a specific
format. See protocol documentation for expected messages.

`subscription-transport-ws` is deprecated. In order to use it, you need to explicitly opt-in by specifying `graphql.subscriptions.protocol=APOLLO_SUBSCRIPTIONS_WS`
configuration property. Support for this protocol will be removed in the next major release.

## Subscription Hooks

In line with the Apollo protocol, we have implemented hooks to execute functions at different stages of the connection lifecycle.
If you would like to implement your own subscription hooks, you can provide a primary spring bean for `ApolloSubscriptionHooks` that overrides the [default one](./spring-beans.md) which do not perform any actions.

### `onConnect` / `onConnectWithContext`
Allows validation of connectionParams prior to starting the connection.
You can reject the connection by throwing an exception.
If you need to forward state to execution, update and return the [GraphQLContext](./spring-graphql-context.md).

### `onOperation` / `onOperationWithContext`
Called when the client executes a GraphQL operation. The context can not be updated here, it is read only.

### `onOperationComplete`
Called when client's unsubscribes

### `onDisconnect`
Called when the client disconnects

## Example

You can see an example implementation of a `Subscription` in the [example app](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/server/spring-server/src/main/kotlin/com/expediagroup/graphql/examples/server/spring/subscriptions/SimpleSubscription.kt).
