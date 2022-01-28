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
[FunctionDataFetcher](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/execution/FunctionDataFetcher.kt)
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

If you want to use a different monad type, like `Single` from [RxJava](https://github.com/ReactiveX/RxJava) or `Mono` from
[Project Reactor](https://projectreactor.io/), you have to:

1.  Create custom `SchemaGeneratorHook` that implements `willResolveMonad` to provide the necessary logic
    to correctly unwrap the monad and return the inner class to generate valid schema

```kotlin
class MonadHooks : SchemaGeneratorHooks {
    override fun willResolveMonad(type: KType): KType = when (type.classifier) {
        Mono::class -> type.arguments.firstOrNull()?.type
        else -> type
    } ?: type
}
```

2.  Provide custom data fetcher that will properly process those monad types.

```kotlin
class CustomFunctionDataFetcher(target: Any?, fn: KFunction<*>, objectMapper: ObjectMapper) : FunctionDataFetcher(target, fn, objectMapper) {
  override fun get(environment: DataFetchingEnvironment): Any? = when (val result = super.get(environment)) {
    is Mono<*> -> result.toFuture()
    else -> result
  }
}

class CustomDataFetcherFactoryProvider(
    private val objectMapper: ObjectMapper
) : SimpleKotlinDataFetcherFactoryProvider(objectMapper) {

  override fun functionDataFetcherFactory(target: Any?, kFunction: KFunction<*>): DataFetcherFactory<Any> = DataFetcherFactory<Any> {
    CustomFunctionDataFetcher(
      target = target,
      fn = kFunction,
      objectMapper = objectMapper)
  }
}
```

With the above you can then create your schema as follows:

```kotlin
class ReactorQuery {
    fun asynchronouslyDo(): Mono<Int> = Mono.just(1)
}

val configWithReactorMonoMonad = SchemaGeneratorConfig(
  supportedPackages = listOf("myPackage"),
  hooks = MonadHooks(),
  dataFetcherFactoryProvider = CustomDataFetcherFactoryProvider())

toSchema(queries = listOf(TopLevelObject(ReactorQuery())), config = configWithReactorMonoMonad)
```

This will produce

```graphql
type Query {
  asynchronouslyDo: Int
}
```

You can find additional example on how to configure the hooks in our [unit
tests](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/test/kotlin/com/expediagroup/graphql/generator/SchemaGeneratorAsyncTests.kt)
and [example app](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/AsyncQuery.kt).
