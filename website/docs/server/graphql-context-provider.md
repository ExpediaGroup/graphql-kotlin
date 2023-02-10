---
id: graphql-context-provider
title: GraphQLContextProvider
---

:::note
If you are using `graphql-kotlin-spring-server`, see the [Spring specific documentation](./spring-server/spring-graphql-context.md).
:::

`GraphQLContextProvider` provides a generic mechanism for generating a GraphQL context for each request.

```kotlin
sealed interface GraphQLContextProvider<Request> {
    suspend fun generateContext(
        request: Request,
        graphQLRequest: GraphQLServerRequest
    ): GraphQLContext
}
```

There are two possible implementations of `GraphQLContextProvider`:

* GraphQLContextFactory
* GraphQLContextBuilder

## GraphQLContextFactory

Given the generic server request and the parsed graphQLRequest, the interface should attempt to create a `GraphQLContext`
to be used for every new operation.

```kotlin
interface GraphQLContextFactory<Request> : GraphQLContextProvider<Request> {
    override suspend fun generateContext(
        request: Request,
        graphQLRequest: GraphQLServerRequest
    ): GraphQLContext
}
```

## GraphQLContextBuilder
Given the generic server request and the parsed graphQLRequest, the interface should attempt to create a `GraphQContext`, using a list of
entry producers.

Depending on the situation, a `GraphQLContext` could contain a lot of entries, entries that might involve IO operations, or expensive
in-memory operations, the `GraphQLContextBuilder` allows to easily segregate that logic into producers and generate entries of a `GraphQLContext` in
a separate way, allowing the code to be more flexible, testable and maintainable.

```kotlin

fun interface GraphQLContextEntryProducer<Request, out K : Any, out V> {
    suspend fun invoke(
        request: Request,
        graphQLRequest: GraphQLServerRequest,
        accumulator: Map<Any, Any?>
    ): Pair<K, V>?
}

interface GraphQLContextBuilder<Request> : GraphQLContextProvider<Request> {

    val producers: List<GraphQLContextEntryProducer<Request, Any, *>>

    override suspend fun generateContext(
        request: Request,
        graphQLRequest: GraphQLServerRequest,
    ): GraphQLContext =
        producers.fold(mutableMapOf<Any, Any?>()) { accumulator, producer ->
            accumulator.also {
                producer.invoke(request, graphQLRequest, accumulator)?.let { entry ->
                    accumulator += entry
                }
            }
        }.toGraphQLContext()
}
```

See [execution context](../schema-generator/execution/contextual-data.md)
for more info on how the context can be used in the schema functions.

## Coroutine Context

By default, `graphql-kotlin-server` creates a supervisor scope with currently available coroutine context. You can provide
additional context elements using `GraphQLContextProvider` by populating `CoroutineContext::class` entry in the context map
or by implementing `graphQLCoroutineContext()` (deprecated) on a custom context object.

### Creating Coroutine Context with GraphQLContextFactory
```kotlin
@Component
class MyCustomContextFactory : GraphQLContextFactory {
    override suspend fun generateContext(
        request: ServerRequest,
        graphQLRequest: GraphQLServerRequest
    ): GraphQLContext =
        mapOf(
            CoroutineContext::class to MDCContext()
        ).toGraphQLContext()
}

GraphQLContextEntryProducer { request, graphQLRequest, accumulator ->
    CoroutineContext::class to MDCContext()
}
```

### Creating Coroutine Context with GraphQLContextBuilder
```kotlin
@Component
class MyCustomContextBuilder : GraphQLContextBuilder {

    override val producers: List<GraphQLContextEntryProducer<Request, Any, *>> = listOf(
        GraphQLContextEntryProducer { request, graphQLRequest, accumulator ->
            CoroutineContext::class to MDCContext()
        }
    )

    override suspend fun generateContext(
        request: ServerRequest,
        graphQLRequest: GraphQLServerRequest
    ): GraphQLContext =
        super.generateContext(
            request,
            graphQLRequest
        )
}
```

`GraphQLServer` will then attempt to create supervisor coroutine scope by combining current coroutine context with custom
coroutine context provided by the `GraphQLContextProvider`. This scope will then be used by `FunctionDataFetcher` to execute
all suspendable functions.

## Suspendable Provider
The interface is marked as a `suspend` function to allow the asynchronous fetching of context data.
This may be helpful if you need to call some other services to calculate a context value.

## Server-Specific Abstractions

A specific `graphql-kotlin-*-server` library may provide an interface on top of this interface so users only have to
be concerned with the context class and not the server class type.
For example the `graphql-kotlin-spring-server` provides the following interfaces, which sets the request type:

```kotlin
interface SpringGraphQLContextFactory : GraphQLContextFactory<ServerRequest>

interface SpringGraphQLContextBuilder : GraphQLContextBuilder<ServerRequest>
```

Another example is the `graphql-kotlin-ktor-server` that provides the following interfaces, also setting the request type:

```kotlin
interface KtorGraphQLContextFactory : GraphQLContextFactory<ApplicationRequest>

interface KtorGraphQLContextBuilder : GraphQLContextBuilder<ApplicationRequest>
```

## HTTP Headers and Cookies

For common use cases around authorization, authentication, or tracing you may need to read HTTP headers and cookies.
This should be done in the `GraphQLContextProvider` and relevant data should be added to the context to be accessible during schema execution.

## Federated Tracing

See [federation tracing support](../schema-generator/federation/federation-tracing.md) documentation for details.

The reference server implementation `graphql-kotlin-spring-server` [supports federated tracing in the context](./spring-server/spring-graphql-context.md).
