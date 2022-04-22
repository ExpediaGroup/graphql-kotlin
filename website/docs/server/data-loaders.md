---
id: data-loaders
title: Data Loaders
---
Data Loaders are a popular caching pattern from the [JavaScript GraphQL implementation](https://github.com/graphql/dataloader).
`graphql-java` provides [support for this pattern](https://www.graphql-java.com/documentation/v16/batching/)
using the `DataLoader` and `DataLoaderRegistry`.

`graphql-kotlin-dataloader` module provides convenient abstractions over the [java-dataloader](https://github.com/graphql-java/java-dataloader).

## `KotlinDataLoader`

To help in the registration of `DataLoaders`, we have created an interface `KotlinDataLoader`:

```kotlin
interface KotlinDataLoader<K, V> {
    val dataLoaderName: String
    fun getBatchLoader(): BatchLoader<K, V>
    fun getOptions(): DataLoaderOptions = DataLoaderOptions.newOptions()
}
```

This allows for library users to still have full control over the creation of the `DataLoader` and his configuration options
and allows server code to handle the registration, generation and execution by request.

## `KotlinDataLoaderRegistryFactory`

The [GraphQLRequestHandler](graphql-request-handler.md) accepts an optional `KotlinDataLoaderRegistryFactory`.
which generates a new `KotlinDataLoaderRegistry` on every request. The registry is a map of a unique data loader names to a `DataLoader` object that handles the cache for an output type in your graph.
A `DataLoader` caches the types by some unique value, usually by the type id, and can handle different types of batch requests.

```kotlin
class UserDataLoader : KotlinDataLoader<Int, User> {
    override val dataLoaderName = "UserDataLoader"
    override fun getBatchLoader() = BatchLoader<Int, User> { ids ->
        CompletableFuture.supplyAsync {
            ids.map { id -> userService.getUser(id) }
        }
    }
    override fun getOptions() = DataLoaderOptions.newOptions().setCachingEnabled(false)
}

class FriendsDataLoader : KotlinDataLoader<Int, List<User>> {
    override val dataLoaderName = "FriendsDataLoader"
    override fun getDataLoader() = BatchLoader<Int, List<User>> { ids ->
        CompletableFuture.supplyAsync {
            ids.map { id ->
                val friends: List<Int> = friendService.getFriends(id)
                userService.getUsers(friends)
            }
        }
    }
}

val dataLoaderRegistryFactory = KotlinDataLoaderRegistryFactory(
    UserDataLoader(), FriendsDataLoader()
)

val dataLoaderRegistry = dataLoaderRegistryFactory.generate()
```

## `KotlinDataLoaderRegistry`

`KotlinDataLoaderRegistry` is a decorator of the original `graphql-java` [DataLoaderRegistry](https://github.com/graphql-java/java-dataloader/blob/master/src/main/java/org/dataloader/DataLoaderRegistry.java)
that provides access to all underlying `DataLoader`s future states. By providing access to cache map containing returned futures,
we get more granular control when to dispatch data loader calls.

## `getDataLoaderFromContext`

`getDataLoaderFromContext` is an extension function that allows you to access to a `KotlinDataLoaderRegistry` stored in the `GraphQLContext`
instead of the `ExecutionInput` with the idea of having the `KotlinDataLoaderRegistry` shared across many `ExecutionInput` in case server
needs to handle a [Batch Request](https://www.apollographql.com/blog/apollo-client/performance/batching-client-graphql-queries/).

You should use this extension function if you are using the `graphql-kotlin` [custom data loader instrumentations](data-loaders-instrumentations.md)

```kotlin
class User(private val id: Int) {
    @GraphQLDescription("Get the users friends using data loader")
    fun getFriends(dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<List<User>> {
        return dataFetchingEnvironment.getDataLoaderFromContext("FriendsDataLoader").load(id)
    }
}
```

## `getValueFromDataLoader`

`graphql-kotlin-server` includes a helpful extension function on the `DataFetchingEnvironment` so that you can easily retrieve values from the data loaders in your schema code.

```kotlin
class User(private val id: ID) {
    @GraphQLDescription("Get the users friends using data loader")
    fun getFriends(dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<List<User>> {
        return dataFetchingEnvironment.getValueFromDataLoader("FriendsDataLoader", id)
    }
}
```

You should use this extension function if you are using the default `graphql-java` DataLoaderDispatcherInstrumentation

:::info
Given that `graphql-java` handles the execution of a GraphQL operation using `CompletableFuture` for async `DataFetchers`,
currently we don't support `suspend` functions when invoking `DataLoader`s.
Instead, return the `CompletableFuture` directly from the `DataLoader` response in your schema functions by taking advantage
of the interoperability between coroutines, reactive streams or any async model to `CompletableFuture`.
See issue [#986](https://github.com/ExpediaGroup/graphql-kotlin/issues/986).
:::
