---
id: spring-graphql-context
title: Generating GraphQL Context
---
`graphql-kotlin-spring-server` provides a Spring specific implementation of `GraphQLContextFactory` and `GraphQLContextBuilder`
to generate a [context](../graphql-context-provider.md).

* `SpringGraphQLContextFactory`
* `SpringGraphQLContextBuilder`

both implementations generate a GraphQL context map with federated tracing information per request.

If you are using `graphql-kotlin-spring-server`, you should extend `DefaultSpringGraphQLContextFactory` or `DefaultSpringGraphQLContextBuilder`
to automatically support federated tracing.


Example extending `DefaultSpringGraphQLContextFactory`
```kotlin
@Component
class MyGraphQLContextFactory : DefaultSpringGraphQLContextFactory() {
    override suspend fun generateContext(
        request: ServerRequest,
        graphQLRequest: GraphQLServerRequest
    ): GraphQLContext =
        super.generateContext(request) + mapOf(
            "myCustomValue" to (request.headers().firstHeader("MyHeader") ?: "defaultContext")
        )
}
```

Example extending `DefaultSpringGraphQLContextFactory`
```kotlin

@Component
class MyCustomValueProducer : GraphQLContextEntryProducer<ServerRequest, String, String> {
    override fun invoke(
        request: ServerRequest,
        graphQLRequest: GraphQLServerRequest,
        accumulator: Map<Any, Any?>
    ): Pair<String, String> =
        "myCustomValue" to (request.headers().firstHeader("MyHeader") ?: "defaultContext")
}

@Component
class MyGraphQLContextBuilder(
    override val producers: List<GraphQLContextEntryProducer<ServerRequest, Any, Any>>
) : DefaultSpringGraphQLContextFactory() {
    override suspend fun generateContext(
        request: ServerRequest,
        graphQLRequest: GraphQLServerRequest
    ): GraphQLContext =
        super.generateContext(request, graphQLRequest)
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
