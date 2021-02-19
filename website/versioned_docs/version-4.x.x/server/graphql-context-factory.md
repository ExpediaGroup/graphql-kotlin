---
id: graphql-context-factory
title: GraphQLContextFactory
original_id: graphql-context-factory
---
Similar to the [GraphQLRequestParser](./graphql-request-parser.md), `GraphQLContextFactory` has a generic method for handling the `Request` and the `GraphQLContext`.

```kotlin

interface GraphQLContextFactory<out Context : GraphQLContext, Request> {
    fun generateContext(request: Request): Context?
}

```

Given the server request, the interface should create the custom `GraphQLContext` class to be used for every new operation.
The context must implement the `GraphQLContext` interface from `graphql-kotlin-schema-generator`.
See [execution context](../schema-generator/execution/contextual-data.md) for more info on how the context can be used in the schema functions.

A specific `graphql-kotlin-*-server` library may provide an abstract class on top of this interface so users only have to be concerned with the context.

For example the `graphql-kotlin-spring-server` provides the following class, which sets the request type:

```kotlin

abstract class SpringGraphQLContextFactory<out T : GraphQLContext> : GraphQLContextFactory<T, ServerRequest>

```

## HTTP Headers

For common use cases around authorization, authentication, or tracing you may need to read HTTP headers.
This should be done in the `GraphQLContextFactory` and relevant data should be added to the context to be accessible during schema exectuion.
