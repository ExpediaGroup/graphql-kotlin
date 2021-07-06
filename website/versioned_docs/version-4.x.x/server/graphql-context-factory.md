---
id: graphql-context-factory
title: GraphQLContextFactory
---

:::note
If you are using `graphql-kotlin-spring-server`, see the [Spring specific documentation](./spring-server/spring-graphql-context.md).
:::

`GraphQLContextFactory` is a generic method for generating a `GraphQLContext` for each request.

```kotlin
interface GraphQLContextFactory<out Context : GraphQLContext, Request> {
    suspend fun generateContext(request: Request): Context?
}
```

Given the generic server request, the interface should create a `GraphQLContext` class to be used for every new operation.
The context must implement the `GraphQLContext` interface from `graphql-kotlin-schema-generator`.
See [execution context](../schema-generator/execution/contextual-data.md) for more info on how the context can be used in the schema functions.

## Nullable Context

The factory may return `null` if a context is not required for execution. This allows the library to have a default factory that just returns `null`.
If your custom factory never returns `null`, then there is no need to use nullable arguments.
However, if your custom factory may return `null`, you must define the context argument as nullable in the schema functions or a runtime exception will be thrown.

```kotlin
data class CustomContext(val value: String) : GraphQLContext

class CustomFactory : GraphQLContextFactory<CustomContext, ServerRequest> {
    suspend fun generateContext(request: Request): Context? {
        if (isSpecialRequest(request)) {
            return null
        }

        val value = callSomeSuspendableService(request)
        return CustomContext(value)
    }
}

class MyQuery : Query {

    fun getResults(context: CustomContext?, input: String): String {
        if (context != null) {
            return getDataWithContext(input, context)
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
This should be done in the `GraphQLContextFactory` and relevant data should be added to the context to be accessible during schema exectuion.

## Federated Tracing

If you need [federation tracing support](../schema-generator/federation/federation-tracing.md), the context must implement the separate `FederatedGraphQLContext` interface from `graphql-kotlin-federation`.

The reference server implementation `graphql-kotlin-spring-server` [supports federated tracing in the context](./spring-server/spring-graphql-context.md).
