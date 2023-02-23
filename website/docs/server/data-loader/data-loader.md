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

`graphql-java` relies on `CompletableFuture`s for scheduling and asynchronously executing GraphQL operations.
While we can provide native support for coroutines for data fetchers (aka field resolvers) because they are resolved
independently, we cannot easily provide native support for the `DataLoader` pattern as it relies
on `CompletableFuture` state machine internals and we cannot update it to use coroutines without fully rewriting
GraphQL Java execution engine.

If you would like to use `DataLoader` pattern in your project, you have to update your data fetchers (aka field resolvers) to return
`CompletableFuture` from the invoked `DataLoader`.

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

When we execute the above query, we will end up calling `UserService#getUser` twice which will result in two independent
downstream service/database calls. This problem is called N+1 problem. By using `DataLoader` pattern,
we can solve this problem and only make a single downstream request/query.

Lets create the `UserDataLoader`:

```kotlin
class UserDataLoader : KotlinDataLoader<ID, User> {
    override val dataLoaderName = "UserDataLoader" // 1
    override fun getDataLoader() = // 2
        DataLoaderFactory.newDataLoader<Int, User> { ids, batchLoaderEnvironment ->
            val coroutineScope = // 3
                batchLoaderEnvironment.getGraphQLContext()?.get<CoroutineScope>()
                    ?: CoroutineScope(EmptyCoroutineContext) // 4

            coroutineScope.future { // 5
                userService.getUsers(ids)
            }
        }
}

```

There are some things going on here:

1. We define the `UserDataLoader` with name "UserDataLoader".
2. The `KotlinDataLoader#getDataLoader()` method returns a `DataLoader<Int, User>`, which `BatchLoader` function should return a `List<User>`.
3. Given that we **don't want** to change our `UserService` async model that is using coroutines, we need a `CoroutineScope`, [which is conveniently available](../../schema-generator/execution/async-models/#coroutines) in the `GraphQLContext` and accessible through [`DataFetchingEnvironment#getGraphQLContext()`](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/executions/graphql-kotlin-dataloader-instrumentation/src/main/kotlin/com/expediagroup/graphql/dataloader/instrumentation/extensions/BatchLoaderEnvironmentExtensions.kt#L43) extension function.
4. After retrieving the `CoroutineScope` from the `batchLoaderEnvironment` we will be able to execute the `userService.getUsers(ids)` suspendable function.
5. We interoperate the suspendable function result to a `CompletableFuture` using [coroutineScope.future](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-jdk8/kotlinx.coroutines.future/future.html).

Finally, we need to update `user` field resolver, to return the `CompletableFuture<User>` from the invoked `DataLoader`.
Make sure to update method signature to also accept the `dataFetchingEnvironment` as you need to pass it to `DataLoader#load` method to be able to execute the request in appropriate coroutine scope.

```kotlin
class MyQuery(
    private val userService: UserService
) : Query {
    fun getUser(id: Int, dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<User> =
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
