---
id: contextual-data
title: Contextual Data
---

All GraphQL servers have a concept of a "context". A GraphQL context contains metadata that is useful to the GraphQL
server, but shouldn't necessarily be part of the GraphQL query's API. A prime example of something that is appropriate
for the GraphQL context would be trace headers for an OpenTracing system such as
[Haystack](https://expediadotcom.github.io/haystack). The GraphQL query itself does not need the information to perform
its function, but the server itself needs the information to ensure observability.

The contents of the GraphQL context vary across applications and it is up to the GraphQL server developers to decide
what it should contain. For Spring based applications, `graphql-kotlin-spring-server` provides a simple mechanism to
build context per query execution through
[GraphQLContextFactory](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/GraphQLContextFactory.kt).
Once context factory bean is available in the Spring application context it will then be used in a corresponding
[ContextWebFilter](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ContextWebFilter.kt)
to populate GraphQL context based on the incoming request and make it available during query execution.

Once your application is configured to build your custom `MyGraphQLContext`, simply add `@GraphQLContext` annotation to
any function argument and the corresponding GraphQL context from the environment will be automatically injected during
execution.

```kotlin
class ContextualQuery {

    fun contextualQuery(
        value: Int,
        @GraphQLContext context: MyGraphQLContext
    ): ContextualResponse = ContextualResponse(value, context.myCustomValue)
}
```

The above query would produce the following GraphQL schema:

```graphql
schema {
  query: Query
}

type Query {
  contextualQuery(
    value: Int!
  ): ContextualResponse!
}
```

Note that the `@GraphQLContext` annotated argument is not reflected in the GraphQL schema.
