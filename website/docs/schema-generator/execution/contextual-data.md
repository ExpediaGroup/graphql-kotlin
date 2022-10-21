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
what it should contain. `graphql-kotlin-server` provides a simple mechanism to build a context per operation with the
[GraphQLContextFactory](../../server/graphql-context-factory.md).
If a custom factory is defined, it will then be used to populate GraphQL context based on the incoming request and make
it available during execution.

## GraphQL Context Map
In graphql-java v17 a new context map was added to the `DataFetchingEnvironment`. This is now the way of saving info for execution, and
you can access this map through the [DataFetchingEnvironment](./data-fetching-environment.md).

```kotlin
class ContextualQuery : Query {
    fun contextualQuery(
        dataFetchingEnvironment: DataFetchingEnvironment,
        value: Int
    ): String =
        "The custom value was ${dataFetchingEnvironment.graphQLContext.get("foo")} and the value was $value"
}
```
