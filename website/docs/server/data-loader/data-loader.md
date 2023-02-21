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
    fun getDataLoader(): DataLoader<K, V>
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
    override fun getDataLoader() = DataLoaderFactory.newDataLoader<ID, User> { ids ->
        CompletableFuture.supplyAsync {
            ids.map { id -> userService.getUser(id) }
        }
    }
}

class FriendsDataLoader : KotlinDataLoader<ID, List<User>> {
    override val dataLoaderName = "FriendsDataLoader"
    override fun getDataLoader() = DataLoaderFactory.newDataLoader<ID, User>(
        { ids ->
            CompletableFuture.supplyAsync {
                ids.map { id ->
                    val friends: List<ID> = friendService.getFriends(id)
                    userService.getUsers(friends)
                }
            }
        },
        DataLoaderOptions.newOptions().setCachingEnabled(false)
    )
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

## DataLoaders and Coroutines

`graphql-java` relies on `CompletableFuture` for scheduling and execute asynchronous field resolvers (aka data fetchers),
`graphql-java` deliberately aims for near zero dependencies which means no Kotlin / WebFlux / RxJava.

Similar to `asynchronous` field resolvers (aka data fetcher), `DataLoader` pattern implementation in `graphql-java` works with `CompletableFuture`,
and because of the listed `graphql-java` constrains we don't provide any native support for `DataLoader` pattern using suspendable functions. Instead, return
the `CompletableFuture` directly from your `DataLoader`s. See issue [#986](https://github.com/ExpediaGroup/graphql-kotlin/issues/986).

### Example

Consider the following query:

```graphql
fragment UserFragment on User {
    id
    name
}
query GetUsersFriends {
    user_1: user(id: 1) {
        ...UserFragment
    }
    user_2: user(id: 2) {
        ...UserFragment
    }
}
```

And the corresponding code that will autogenerate schema:

```kotlin
class MyQuery(
    private val userService: UserService
) : Query {
    suspend fun getUser(id: Int): User = userService.getUser(id)
}

class UserService {
    suspend fun getUser(id: Int): User = // async logic to get user
    suspend fun getUsers(ids: List<Int>): List<User> = // async logic to get users
}
```

When the execution of the query completes we will have a total of 2 requests to the `UserService`, this is where the usage of
`DataLoader` pattern can help to make the execution of query more performant by making only 1 request.

Lets create the `UserDataLoader`:

```kotlin
class UserDataLoader : KotlinDataLoader<ID, User> {
    override val dataLoaderName = "UserDataLoader"
    override fun getDataLoader() =
        DataLoaderFactory.newDataLoader<Int, User> { ids, batchLoaderEnvironment ->
            val coroutineScope =
                batchLoaderEnvironment.getGraphQLContext()?.get<CoroutineScope>()
                    ?: CoroutineScope(EmptyCoroutineContext)

            coroutineScope.future {
                userService.getUsers(ids)
            }
        }
}
```

There are some things going on here:

1. We define the `UserDataLoader` with name "UserDataLoader"
2. The `getLoader()` method returns a `DataLoader<Int, User>`, which `BatchLoader` function should return a `List<User>`
3. Given that we **don't want** to change our `UserService` async model that is using coroutines, we need a `CoroutineScope`, [which is conveniently available](../../schema-generator/execution/async-models/#coroutines) in the `GraphQLContext`, which is provided in the `DataFetchingEnvironment` and accessible with the [getGraphQLContext()](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/executions/graphql-kotlin-dataloader-instrumentation/src/main/kotlin/com/expediagroup/graphql/dataloader/instrumentation/extensions/BatchLoaderEnvironmentExtensions.kt#L43) extension function.
4. After retrieving the `CoroutineScope` from the `batchLoaderEnvironment` we will be able to execute the `userService.getUsers(ids)` suspendable function.
5. We interoperate the suspendable function result to a `CompletableFuture` using [coroutineScope.future](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-jdk8/kotlinx.coroutines.future/future.html)

Finally, the only thing that we need to change is the `user` field resolver, to not be suspendable and
just return the `CompletableFuture<User>` that the `DataLoader`, make sure to pass the `dataFetchingEnvironment` as `keyContext` which is the second argument of `DataLoader.load`

```kotlin
class MyQuery(
    private val userService: UserService
) : Query {
    suspend fun getUser(id: Int, dataFetchingEnvironment: DataFetchingEnvironment): User =
        dataFetchingEnvironment
            .getDataLoader<Int, Mission>("UserDataLoader")
            .load(id, dataFetchingEnvironment)
}

class UserService {
    suspend fun getUser(id: Int): User {
        // logic to get user
    }
    suspend fun getUsers(ids: List<Int>): List<User> {
        // logic to get users, this method is called from the DataLoader
    }
}
```
