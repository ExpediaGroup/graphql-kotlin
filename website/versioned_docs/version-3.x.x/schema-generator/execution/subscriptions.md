---
id: subscriptions
title: Subscriptions
original_id: subscriptions
---
Subscriptions are supported with `graphql-java`. See their documentation first:

https://www.graphql-java.com/documentation/v16/subscriptions/

To make a function a subscription function update the return type to a type wrapped in an implementation of a
reactive-streams `Publisher<T>`. As an example here is a function that uses Spring WebFlux to return a random number every
second. Since it is an implementation of `Publisher` this is a valid method.

```kotlin
fun counter(): Flux<Int> = Flux.interval(Duration.ofSeconds(1)).map { Random.nextInt() }
```

### Subscription Hooks

#### `willResolveMonad`

This hook is called after a new subscription type is generated but before it is added to the schema. The other generator hooks are still called so you can add logic for the types and
validation of subscriptions the same as queries and mutations.

#### `isValidSubscriptionReturnType`

This hook is called when generating the functions for each subscription. It allows for changing the rules of what classes can be used as the return type. By default, graphql-java supports ``.

To effectively use this hook, you should also override the `` hook, and if you are using `` you should override the `` bean to specify a custom subscription execution strategy.

### Server Implementation

The server that runs your GraphQL schema will have to support some method for subscriptions, like WebSockets.
`graphql-kotlin-spring-server` provides a default WebSocket based implementation. See more details in the
[server documentation](../../spring-server/subscriptions).
