---
id: graphql-context-factory
title: GraphQLContextFactory
---

:::note
If you are using `graphql-kotlin-spring-server`, see the [Spring specific documentation](./spring-server/spring-graphql-context.md).
:::

`GraphQLContextFactory` provides a generic mechanism for generating a GraphQL context for each request.

```kotlin
interface GraphQLContextFactory<Request> {
    suspend fun generateContext(request: Request): GraphQLContext =
        emptyMap<Any, Any>().toGraphQLContext()
}
```

Given the generic server request, the interface should attempt to create a `GraphQLContext` to be used for every new operation.
interface from `graphql-kotlin-schema-generator`. See [execution context](../schema-generator/execution/contextual-data.md)
for more info on how the context can be used in the schema functions.

## Coroutine Context

By default, `graphql-kotlin-server` creates a supervisor scope with currently available coroutine context. You can provide
additional context elements using `GraphQLContextFactory` by populating `CoroutineContext::class` entry in the context map
or by implementing `graphQLCoroutineContext()` (deprecated) on a custom context object.

```kotlin
@Component
class MyCustomContextFactory : GraphQLContextFactory() {
    override suspend fun generateContext(request: ServerRequest): GraphQLContext =
        mapOf(
            CoroutineContext::class to MDCContext()
        ).toGraphQLContext()
}
```

`GraphQLServer` will then attempt to create supervisor coroutine scope by combining current coroutine context with custom
coroutine context provided by the `GraphQLContextFactory`. This scope will then be used by `FunctionDataFetcher` to execute
all suspendable functions.

## Suspendable Factory
The interface is marked as a `suspend` function to allow the asynchronous fetching of context data.
This may be helpful if you need to call some other services to calculate a context value.

## Server-Specific Abstractions

A specific `graphql-kotlin-*-server` library may provide an abstract class on top of this interface so users only have to
be concerned with the context class and not the server class type.
For example the `graphql-kotlin-spring-server` provides the following class, which sets the request type:

```kotlin
abstract class SpringGraphQLContextFactory : GraphQLContextFactory<ServerRequest>
```

## HTTP Headers and Cookies

For common use cases around authorization, authentication, or tracing you may need to read HTTP headers and cookies.
This should be done in the `GraphQLContextFactory` and relevant data should be added to the context to be accessible during schema execution.

## Federated Tracing

See [federation tracing support](../schema-generator/federation/federation-tracing.md) documentation for details.

The reference server implementation `graphql-kotlin-spring-server` [supports federated tracing in the context](./spring-server/spring-graphql-context.md).
