---
id: version-4.0.0-alpha.11-data-loader-registry-factory
title: DataLoaderRegistryFactory
original_id: data-loader-registry-factory
---

[Data loaders](https://github.com/graphql/dataloader) are a popular caching pattern from the JavaScript GraphQL implementation.
`graphql-java` provides [support for this pattern](https://www.graphql-java.com/documentation/v16/batching/) using the `DataLoader` and `DataLoaderRegistry`.

Since `graphql-kotlin` allows you to abstract the schema generation and data fetching code, you may not even need data loaders if instead you have some persistant cache on your server.

```kotlin
class User(val id: ID) {

    // The friendService and userService, which have nothing to do with GraphQL,
    // should be concerned with caching and batch calls instead of your schema classes
    fun getFriends(): List<User> {
        val friends: List<ID> = friendService.getFriends(id)
        return userService.getUsers(friends)
    }

}
```

If you still want to use data loaders though, they are supported through the common interfaces.

## `DataLoaderRegistryFactory`

Data loaders should be created per-request as the caching pattern may not work for serving multiple users requesting data simultaneously.
The [GraphQLRequestHandler](./graphql-request-handler.md) accepts an optional `DataLoaderRegistryFactory` interface that will be called on every request to get a `DataLoaderRegistry`.

```kotlin
interface DataLoaderRegistryFactory {
    fun generate(): DataLoaderRegistry
}
```

The `DataLoaderRegistry` is a map of a unique keys to a `DataLoader` object that handles the cache for an output type in your graph.
A `DataLoader` caches the types by some unique value, usually by the type id.

```kotlin
class MyCustomDataLoaderRegistryFactory : DataLoaderRegistryFactory {

    private val userLoader = DataLoader<ID, User> { id ->
        CompletableFuture.supplyAsync { userService.getUser(id) }
    }

    override fun generate(): DataLoaderRegistry {
        val registry = DataLoaderRegistry()
        registry.register("userLoader", userLoader)
        return registry
    }
}
```

> NOTE: Because the execution of data loaders is handled by `graphql-java`, which runs using `CompletionStage`, currently we do not support `suspend` functions.
> See issue [#986](https://github.com/ExpediaGroup/graphql-kotlin/issues/986).
