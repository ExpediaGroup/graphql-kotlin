---
id: graphql-context-factory
title: GraphQLContextFactory
---

:::note
If you are using `graphql-kotlin-spring-server`, see the [Spring specific documentation](./spring-server/spring-graphql-context.md).
:::

`GraphQLContextFactory` provides a generic mechanism for generating a GraphQL context for each request.

```kotlin
interface GraphQLContextFactory<out Context : GraphQLContext, Request> {
    @Deprecated("use generateContextMap instead")
    suspend fun generateContext(request: Request): Context? = null
    suspend fun generateContextMap(request: Request): Map<*, Any> = emptyMap<Any, Any>()
}
```

Given the generic server request, the interface should attempt to create a legacy `GraphQLContext` class (could be null)
and a new context map to be used for every new operation. The legacy context class must implement the `GraphQLContext`
interface from `graphql-kotlin-schema-generator`. See [execution context](../schema-generator/execution/contextual-data.md)
for more info on how the context can be used in the schema functions.

## Coroutine Context

By default, `graphql-kotlin-server` creates a supervisor scope with currently available coroutine context. You can provide
additional context elements using `GraphQLContextFactory` by populating `CoroutineContext::class` entry in the context map
or by implementing `graphQLCoroutineContext()` (deprecated) on a custom context object.

```kotlin
@Component
class MyCustomContextFactory : GraphQLContextFactory() {
    override suspend fun generateContextMap(request: ServerRequest): Map<*, Any> = mapOf(
        CoroutineContext::class to MDCContext()
    )
}
```

`GraphQLServer` will then attempt to create supervisor coroutine scope by combining current coroutine context with custom
coroutine context provided by the `GraphQLContextFactory`. This scope will then be used by `FunctionDataFetcher` to execute
all suspendable functions.

## Nullable Context (Deprecated)

:::danger
Instead of creating nullable instance of a custom GraphQL context, please migrate to use new GraphQL context map. Support
for arbitrary GraphQL context will be removed in future releases.
:::

The factory may return `null` GraphQL context object if it is not required for execution. If your custom factory never
returns `null`, then there is no need to use nullable arguments. However, if your custom factory may return `null`, you
must define the context argument as nullable in the schema functions or a runtime exception will be thrown.

```kotlin
@Deprecated
data class CustomContext(val value: String) : GraphQLContext

class CustomFactory : GraphQLContextFactory<CustomContext, ServerRequest> {
    override suspend fun generateContext(request: ServerRequest): Context? {
        if (isSpecialRequest(request)) {
            return null
        }

        val value = callSomeSuspendableService(request)
        return CustomContext(value)
    }

    override suspend fun generateContextMap(request: ServerRequest): Map<*, Any> {
        if (isSpecialRequest(request)) {
            return emptyMap<Any, Any>()
        }

        val value = callSomeSuspendableService(request)
        return mapOf("myKey" to value)
    }
}

class MyQuery : Query {

    fun getResults(context: CustomContext? = null, dfe: DataFetchingEnvironment, input: String): String {
        val contextMapValue = dfe.graphQLContext.get("myKey")
        val contextObjectValue = context?.value
        val contextValue = contextMapValue ?: contextObjectValue
        if (contextValue != null) {
            return getDataWithContextValue(input, contextValue)
        }

        return getBasicData(input)
    }
}
```

## Suspendable Factory
The interface is marked as a `suspend` function to allow the asynchronous fetching of context data.
This may be helpful if you need to call some other services to calculate a context value.

## Server-Specific Abstractions

A specific `graphql-kotlin-*-server` library may provide an abstract class on top of this interface so users only have to be concerned with the context class and not the server class type.
For example the `graphql-kotlin-spring-server` provides the following class, which sets the request type:

```kotlin
abstract class SpringGraphQLContextFactory<out T : GraphQLContext> : GraphQLContextFactory<T, ServerRequest>
```

## HTTP Headers and Cookies

For common use cases around authorization, authentication, or tracing you may need to read HTTP headers and cookies.
This should be done in the `GraphQLContextFactory` and relevant data should be added to the context to be accessible during schema execution.

## Federated Tracing

See [federation tracing support](../schema-generator/federation/federation-tracing.md) documentation for details.

The reference server implementation `graphql-kotlin-spring-server` [supports federated tracing in the context](./spring-server/spring-graphql-context.md).
