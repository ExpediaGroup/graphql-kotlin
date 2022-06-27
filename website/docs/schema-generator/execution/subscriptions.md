---
id: subscriptions
title: Subscriptions
---
Subscriptions are supported with `graphql-java`. See their documentation first:

https://www.graphql-java.com/documentation/subscriptions

To make a function a subscription function you just have to have the return type wrapped in an implementation of a
reactive-streams `Publisher<T>`. As an example, here is a function that uses Spring WebFlux to return a random number every
second. Since `Flux` is an implementation of `Publisher` this is a valid method.

```kotlin
fun counter(): Flux<Int> = Flux.interval(Duration.ofSeconds(1)).map { Random.nextInt() }
```

Then in the `toSchema` method you just have to provide a `List<TopLevelObject>` the same way as queries and mutations
are provided with the `subscriptions` argument.

```kotlin
toSchema(
    config = schemaConfig,
    queries = queries.toTopLevelObjects(),
    mutations = mutations.toTopLevelObjects(),
    subscriptions = subscriptions.toTopLevelObjects()
)
```

## Flow Support

`graphql-kotlin` provides support for Kotlin `Flow` through `FlowSubscriptionSchemaGeneratorHooks` and `FlowSubscriptionExecutionStrategy`.
Both hooks and execution strategy have to be configured in order to support `Flow` in your GraphQL server.

`FlowSubscriptionSchemaGeneratorHooks` are custom hooks that provide support for using `Flow` return type within the
GraphQL server.

`FlowSubscriptionExecutionStrategy` is a reimplementation of the `graphql-java` default `SubscriptionExecutionStrategy`
that adds support for handling Kotlin `Flow` types. Thanks to the Kotlin coroutines interoperability, this strategy works
with any `Publisher` and will automatically convert any `Flow`s to a `Publisher`.

## Subscription Hooks

### `willResolveMonad`

This hooks is called before resolving Kotlin return type to a GraphQL type and can be used to provide support for additional
monads (e.g. Kotlin `Flow`).

### `didGenerateSubscriptionType`
This hook is called after a new subscription type is generated but before it is added to the schema. The other generator
hooks are still called so you can add logic for the types and validation of subscriptions the same as queries and mutations.

### `isValidSubscriptionReturnType`
This hook is called when generating the functions for each subscription. It allows for changing the rules of what classes
can be used as the return type. By default, graphql-java supports `org.reactivestreams.Publisher`.

To effectively use this hook, you should also override the `willResolveMonad` hook to support the additional subscription
return type. Your GraphQL server may also require a custom subscription execution strategy in order to process it at runtime.

## Server Implementation

The server that runs your GraphQL schema will have to support some method for subscriptions, like WebSockets.
`graphql-kotlin-spring-server` provides a default WebSocket based implementation. See more details in the
[server documentation](../../server/server-subscriptions.md).
