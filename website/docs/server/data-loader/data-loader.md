---
id: data-loader
title: Data Loaders
---
Data Loaders are a popular caching pattern from the [JavaScript GraphQL implementation](https://github.com/graphql/dataloader).
`graphql-java` provides [support for this pattern](https://www.graphql-java.com/documentation/v16/batching/)
using the `DataLoader` and `DataLoaderRegistry`.

Since `graphql-kotlin` allows you to abstract the schema generation and data fetching code, you may not even need
data loaders if instead you have some persistant cache on your server.

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

This allows for library users to still have full control over the creation of the `DataLoader` and its various configuration
options but also allows common server code to handle the registration, generation and execution of the request.

## `KotlinDataLoaderRegistryFactory`

The [GraphQLRequestHandler](../graphql-request-handler.md) accepts an optional `KotlinDataLoaderRegistryFactory`.
which generates a new `KotlinDataLoaderRegistry` on every request. The registry is a map of a unique data loader names to a `DataLoader` object that handles the cache for an output type in your graph.
A `DataLoader` caches the types by some unique value, usually by the type id, and can handle different types of batch requests.

```kotlin
class UserDataLoader : KotlinDataLoader<ID, User> {
    override val dataLoaderName = "UserDataLoader"
    override fun getOptions() = DataLoaderOptions.newOptions().setCachingEnabled(false)
    override fun getBatchLoader() = BatchLoader<ID, User> { ids ->
        CompletableFuture.supplyAsync {
            ids.map { id -> userService.getUser(id) }
        }
    }
}

class FriendsDataLoader : KotlinDataLoader<ID, List<User>> {
    override val dataLoaderName = "FriendsDataLoader"
    override fun getDataLoader() = BatchLoader<ID, List<User>> { ids ->
        CompletableFuture.supplyAsync {
            ids.map { id ->
                val friends: List<ID> = friendService.getFriends(id)
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

[KotlinDataLoaderRegistry](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/executions/graphql-kotlin-dataloader/src/main/kotlin/com/expediagroup/graphql/dataloader/KotlinDataLoaderRegistry.kt)
is a decorator of the original `graphql-java` [DataLoaderRegistry](https://github.com/graphql-java/java-dataloader/blob/master/src/main/java/org/dataloader/DataLoaderRegistry.java)
that keeps track of all underlying `DataLoader`s futures. By keeping track of to cache map containing returned futures,
we get more granular control when to dispatch data loader calls.

## `getValueFromDataLoader`

`graphql-kotlin-server` includes a helpful extension function on the `DataFetchingEnvironment` so that you can easily retrieve values from the data loaders in your schema code.

```kotlin
class User(val id: ID) {
    @GraphQLDescription("Get the users friends using data loader")
    fun getFriends(dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<List<User>> {
        return dataFetchingEnvironment.getValueFromDataLoader("FriendsDataLoader", id)
    }
}
```

:::info
Given that `graphql-java` relies on `CompletableFuture`s for scheduling and asynchronous execution of `DataLoader` calls,
currently we don't provide any native support for `DataLoader` pattern using coroutines. Instead, return
the `CompletableFuture` directly from your `DataLoader`s. See issue [#986](https://github.com/ExpediaGroup/graphql-kotlin/issues/986).
:::
