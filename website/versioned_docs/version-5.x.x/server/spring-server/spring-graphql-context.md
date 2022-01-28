---
id: spring-graphql-context
title: Generating GraphQL Context
---
`graphql-kotlin-spring-server` provides a Spring specific implementation of [GraphQLContextFactory](../graphql-context-factory.md) and the context.

* `SpringGraphQLContext` - Implements the Spring `ServerRequest` and federation tracing `HTTPRequestHeaders`
* `SpringGraphQLContextFactory` - Generates a `SpringGraphQLContext` per request

If you are using `graphql-kotlin-spring-server`, you should extend `SpringGraphQLContext` and `SpringGraphQLContextFactory` to maintain support with all the other features.

```kotlin
class MyGraphQLContext(val myCustomValue: String, request: ServerRequest) : SpringGraphQLContext(request)

@Component
class MyGraphQLContextFactory : SpringGraphQLContextFactory<MyGraphQLContext>() {
    override suspend fun generateContext(request: ServerRequest): MyGraphQLContext {
        val customVal = request.headers().firstHeader("MyHeader") ?: "defaultValue"
        return MyGraphQLContext(customVal, request)
    }
}
```

Once your application is configured to build your custom `MyGraphQLContext`, you can then specify it as function argument.
While executing the query, the corresponding GraphQL context will be read from the environment and automatically injected to the function input arguments.
This argument will not appear in the GraphQL schema.

For more details, see the [Contextual Data Documentation](../../schema-generator/execution/contextual-data.md).

## Federated Context

If you need [federation tracing support](../../schema-generator/federation/federation-tracing.md), you can set the appropiate [configuration properties](./spring-properties.md).
The provided `SpringGraphQLContext` implements the required federation methods for tracing, so as long as you extend this context class you will maintain feature support.
