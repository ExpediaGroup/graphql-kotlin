---
id: ktor-graphql-context
title: Generating GraphQL Context
---

`graphql-kotlin-ktor-server` provides a Ktor specific implementation of [GraphQLContextFactory](../graphql-context-factory.md)
and the context.

* `KtorGraphQLContextFactory` - Generates GraphQL context map with federated tracing information per request

If you are using `graphql-kotlin-ktor-server`, you should extend `DefaultKtorGraphQLContextFactory` to automatically
support federated tracing.

```kotlin
class CustomGraphQLContextFactory : DefaultKtorGraphQLContextFactory() {
    override suspend fun generateContext(request: ApplicationRequest): GraphQLContext =
        super.generateContext(request).plus(
            mapOf("myCustomValue" to (request.headers["my-custom-header"] ?: "defaultContext"))
        )
}
```

Once your application is configured to build your custom GraphQL context, you can then access it through a data fetching
environment argument. While executing the query, data fetching environment will be automatically injected to the function input arguments.
This argument will not appear in the GraphQL schema.

For more details, see the [Contextual Data Documentation](../../schema-generator/execution/contextual-data.md).

## Federated Context

If you need [federation tracing support](../../schema-generator/federation/federation-tracing.md), you can set the appropriate [configuration properties](./ktor-configuration.md).
The provided `DefaultKtorGraphQLContextFactory` populates the required information for federated tracing, so as long as
you extend this context class you will maintain feature support.
