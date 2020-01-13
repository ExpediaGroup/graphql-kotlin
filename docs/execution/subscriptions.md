---
id: subscriptions
title: Subscriptions
---
Subscriptions are supported with `graphql-java`. See their documentation first:
https://www.graphql-java.com/documentation/v11/subscriptions/

To make a function a subscription function you just have to have the return type wrapped in an implementation of a
reactive-streams `Publisher`. As an example here is a function that uses Spring WebFlux to return a random number every
second. Since `Flux` is an implementation of `Publisher` this is a valid method.

```kotlin
@GraphQLDescription("Returns a random number every second")
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

### Subscription Hooks

Through the hooks a new method was added `didGenerateSubscriptionType` which is called after a new subscription type is
generated but before it is added to the schema. The other hook are still called so you can add logic for the types and
validation of subscriptions the same as queries and mutations.

### Server Implementation

The server that runs your GraphQL schema will have to support some method for subscriptions, like WebSockets.
`graphql-kotlin-spring-server` provides a default WebSocket based implementation. See more details in the
[server documentation](../spring-server/subscriptions).
