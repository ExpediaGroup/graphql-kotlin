---
id: spring-graphql-context
title: Generating GraphQL Context
---

`graphql-kotlin-spring-server` provides a simple mechanism to build [GraphQL context](../execution/contextual-data) per query execution through
[GraphQLContextFactory](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/GraphQLContextFactory.kt).
Once context factory bean is available in the Spring application context it will then be used in a corresponding
[ContextWebFilter](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ContextWebFilter.kt)
to populate GraphQL context based on the incoming request and make it available during query execution.

For example if we define our custom context as follows:

```kotlin
data class MyGraphQLContext(val myCustomValue: String)
```

We can generate corresponding `GraphQLContextFactory` bean:

```kotlin
@Component
class MyGraphQLContextFactory: GraphQLContextFactory<MyGraphQLContext> {
    override suspend fun generateContext(
        request: ServerHttpRequest, 
        response: ServerHttpResponse
    ): MyGraphQLContext = MyGraphQLContext(
        myCustomValue = request.headers.getFirst("MyHeader") ?: "defaultContext"
    )
}
```

Once your application is configured to build your custom `MyGraphQLContext`, we can then specify it as function argument by annotating it with `@GraphQLContext`. 
While executing the query, the corresponding GraphQL context will be read from the environment and automatically injected to the function input arguments.

```kotlin
@Component
class ContextualQuery: Query {
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

Notice that the `@GraphQLContext` annotated argument is not reflected in the generated GraphQL schema.
