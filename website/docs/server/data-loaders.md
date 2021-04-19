---
id: data-loaders
title: Data Loaders
---
Data Loaders are a popular caching pattern from the [JavaScript GraphQL implementation](https://github.com/graphql/dataloader).
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

## `KotlinDataLoader`

The [GraphQLRequestHandler](./graphql-request-handler.md) accepts an optional `DataLoaderRegistryFactory` that will be used on every request.
The `DataLoaderRegistryFactory` generates a new `DataLoaderRegistry` on every request. The registry is a map of a unique data loader names to a `DataLoader` object that handles the cache for an output type in your graph.
A `DataLoader` caches the types by some unique value, usually by the type id, and can handle different types of batch requests.

To help in the registration of these various `DataLoaders`, we have created a basic interface `KotlinDataLoader`:

```kotlin
interface KotlinDataLoader<K, V> {
    val dataLoaderName: String
    fun getDataLoader(): DataLoader<K, V>
}
```

This allows for library users to still have full control over the creation of the `DataLoader` and its various configuraiton options,
but then allows common server code to handle the registration, generation on request, and execution.

```kotlin
class UserDataLoader : KotlinDataLoader<ID, User> {
    override val dataLoaderName = "UserDataLoader"
    override fun getDataLoader() = DataLoader<ID, User>({ ids ->
        CompletableFuture.supplyAsync {
            ids.map { id -> userService.getUser(id) }
        }
    }, DataLoaderOptions.newOptions().setCachingEnabled(false))
}

class FriendsDataLoader : KotlinDataLoader<ID, List<User>> {
    override val dataLoaderName = "FriendsDataLoader"
    override fun getDataLoader() = DataLoader<ID, List<User>> { ids ->
        CompletableFuture.supplyAsync {
            ids.map { id ->
                val friends: List<ID> = friendService.getFriends(id)
                userService.getUsers(friends)
            }
        }
    }
}
```

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
Because the execution of data loaders is handled by `graphql-java`, which runs using `CompletionStage`, currently we can
not support `suspend` functions when envoking data loaders. Instead, return the `CompletableFuture` directly from the `DataLoader`
response in your schema functions. See issue [#986](https://github.com/ExpediaGroup/graphql-kotlin/issues/986).
:::
