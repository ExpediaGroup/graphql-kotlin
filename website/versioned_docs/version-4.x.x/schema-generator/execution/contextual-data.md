---
id: contextual-data
title: Contextual Data
---

All GraphQL servers have a concept of a "context". A GraphQL context contains metadata that is useful to the GraphQL
server, but shouldn't necessarily be part of the GraphQL schema. A prime example of something that is appropriate
for the GraphQL context would be trace headers for an OpenTracing system such as
[Haystack](https://expediadotcom.github.io/haystack). The GraphQL query does not need the information to perform
its function, but the server needs the information to ensure observability.

The contents of the GraphQL context vary across applications and it is up to the GraphQL server developers to decide
what it should contain. `graphql-kotlin-server` provides a simple mechanism to
build a context per operation with the [GraphQLContextFactory](../../server/graphql-context-factory.md).
If a custom factory is defined, it will then be used to populate GraphQL context based on the incoming request and make it available during execution.

## GraphQLContext Interface

The easiest way to specify a context class is to use the `GraphQLContext` marker interface. This interface does not require any implementations,
it is just used to inform the schema generator that this is the class that should be used as the context for every request.

```kotlin
class MyGraphQLContext(val customValue: String) : GraphQLContext
```

Then, you can use the class as an argument and it will be automatically injected during execution time.

```kotlin
class ContextualQuery : Query {
    fun contextualQuery(
        context: MyGraphQLContext,
        value: Int
    ): String = "The custom value was ${context.customValue} and the value was $value"
}
```

The above query would produce the following GraphQL schema:

```graphql
type Query {
  contextualQuery(value: Int!): String!
}
```

Note that the argument that implements `GraphQLContext` is not reflected in the GraphQL schema.

## Handling Context Errors

The [GraphQLContextFactory](../../server/graphql-context-factory.md) may return `null`. If your factory implementation never returns `null`, then there is no need to change your schema.
If the factory could return `null`, then the context arugments in your schema should be nullable so a runtime exception is not thrown.

```kotlin
class ContextualQuery : Query {
    fun contextualQuery(context: MyGraphQLContext?, value: Int): String {
        if (context != null) {
            return "The custom value was ${context.customValue} and the value was $value"
        }

        return "The context was null and the value was $value"
    }
}
```

## Injection Customization

The context is injected into the execution through the `FunctionDataFetcher` class.
If you want to customize the logic on how the context is determined, that is possible to override.
See more details on the [Fetching Data documentation](./fetching-data.md)
