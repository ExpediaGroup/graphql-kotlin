---
id: spring-graphql-context
title: Generating GraphQL Context
---
`graphql-kotlin-spring-server` provides a Spring specific implementation of [GraphQLContextFactory](../graphql-context-factory.md)
and the context.

* `SpringGraphQLContextFactory` - Generates GraphQL context map with federated tracing information per request

If you are using `graphql-kotlin-spring-server`, you should extend `DefaultSpringGraphQLContextFactory` to automatically
support federated tracing.

```kotlin
@Component
class MyGraphQLContextFactory : DefaultSpringGraphQLContextFactory() {
    override suspend fun generateContext(request: ServerRequest): GraphQLContext =
        super.generateContext(request) + mapOf(
            "myCustomValue" to (request.headers().firstHeader("MyHeader") ?: "defaultContext")
        )
}
```

Once your application is configured to build your custom GraphQL context, you can then access it through a data fetching
environment argument. While executing the query, data fetching environment will be automatically injected to the function input arguments.
This argument will not appear in the GraphQL schema.

For more details, see the [Contextual Data Documentation](../../schema-generator/execution/contextual-data.md).

## Federated Context

If you need [federation tracing support](../../schema-generator/federation/federation-tracing.md), you can set the appropriate [configuration properties](./spring-properties.md).
The provided `DefaultSpringGraphQLContextFactory` populates the required information for federated tracing, so as long as
you extend this context class you will maintain feature support.
