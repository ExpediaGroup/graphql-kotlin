---
id: spring-graphql-context
title: Generating GraphQL Context
original_id: spring-graphql-context
---

`graphql-kotlin-spring-server` provides a simple mechanism to build a [GraphQL context](../schema-generator/execution/contextual-data.md) per query execution through
[GraphQLContextFactory](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/GraphQLContextFactory.kt).
Once a context factory bean is available, it will then be used in
[ContextWebFilter](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ContextWebFilter.kt)
to populate the GraphQL context based on the incoming request and make it available during query execution.

For example if we define our custom context as follows:

```kotlin
class MyGraphQLContext(val myCustomValue: String) : GraphQLContext
```

We can generate the corresponding `GraphQLContextFactory` bean:

```kotlin
@Component
class MyGraphQLContextFactory: GraphQLContextFactory<MyGraphQLContext> {
    override suspend fun generateContext(
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): MyGraphQLContext = MyGraphQLContext(
        myCustomValue = request.headers.getFirst("MyHeader") ?: "defaultValue"
    )
}
```

Once your application is configured to build your custom `MyGraphQLContext`, we can then specify it as function argument but it will not be included in the schema.
While executing the query, the corresponding GraphQL context will be read from the environment and automatically injected to the function input arguments.

For more details see the [Contextual Data documentation](../schema-generator/execution/contextual-data).
