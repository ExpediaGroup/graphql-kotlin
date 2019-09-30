---
id: async-models
title: Async Models
---
By default, `graphql-kotlin-schema-generator` will resolve all functions synchronously, i.e. it will block the
underlying thread while executing the target function. While you could configure your GraphQL server with execution
strategies that execute each query in parallel on some thread pools, instead we highly recommend to utilize asynchronous
programming models.

## Coroutines

`graphql-kotlin-schema-generator` has built-in support for Kotlin coroutines. Provided default
[FunctionDataFetcher](https://github.com/ExpediaDotCom/graphql-kotlin/blob/master/graphql-kotlin-schema-generator/src/main/kotlin/com/expedia/graphql/execution/FunctionDataFetcher.kt)
will automatically asynchronously execute suspendable functions and convert the result to `CompletableFuture` expected
by `graphql-java`.

Example

```kotlin
data class User(val id: String, val name: String)

class Query {
    suspend fun getUser(id: String): User {
        // Your coroutine logic to get user data
    }
}
```

will produce the following schema

```graphql

schema {
  query: Query
}

type Query {
  getUser(id: String!): User
}

type User {
  id: String!
  name: String!
}
```

## CompletableFuture

`graphql-java` relies on Java `CompletableFuture` for asynchronously processing the requests. In order to simplify the
interop with `graphql-java`, `graphql-kotlin-schema-generator` has a built-in hook which will automatically unwrap a
`CompletableFuture` and use the inner class as the return type in the schema.

```kotlin
data class User(val id: String, val name: String)

class Query {
    fun getUser(id: String): CompletableFuture<User> {
        // Your logic to get data asynchronously
    }
}
```

will result in the exactly the same schema as in the coroutine example above.

## RxJava/Reactor

If you use a different monad type, like `Single` from [RxJava](https://github.com/ReactiveX/RxJava) or `Mono` from
[Project Reactor](https://projectreactor.io/), you just have to provide the logic in
`SchemaGeneratorHooks.willResolveMonad` to unwrap it and return the inner class.

```kotlin
class RxJava2Query {
    fun asynchronouslyDo(): Observable<Int> = Observable.just(1)

    fun asynchronouslyDoSingle(): Single<Int> = Single.just(1)

    fun maybe(): Maybe<Int> = Maybe.empty()
}

private class MonadHooks : SchemaGeneratorHooks {
    override fun willResolveMonad(type: KType): KType = when (type.classifier) {
        Observable::class, Single::class, Maybe::class -> type.arguments.firstOrNull()?.type
        else -> type
    } ?: type
}

val configWithRxJavaMonads = getConfig(hooks = MonadHooks())

toSchema(queries = listOf(TopLevelObject(RxJava2Query())), config = configWithRxJavaMonads)
```

This will produce

```graphql
type Query {
  asynchronouslyDo(): Int
  asynchronouslyDoSingle(): Int
  maybe: Int
}
```

You can find additional example on how to configure the hooks in our [unit
tests](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-schema-generator/src/test/kotlin/com/expediagroup/graphql/generator/SchemaGeneratorAsyncTests.kt).
