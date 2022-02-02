---
id: federation-tracing
title: Federation Tracing
---

`graphql-kotlin-federation` module relies on [apollographql/federation-jvm](https://github.com/apollographql/federation-jvm)
package to provide support for Apollo Federation tracing. Tracing is turned on by including `FederatedTracingInstrumentation`
in your GraphQL instance. In order for the `FederatedTracingInstrumentation` to know whether incoming request should be
traced, we need to provide it a `apollo-federation-include-trace` header value.

```kotlin
val schema = toFederatedSchema(myFederatedConfig, listOf(TopLevelObject(MyFederatedQuery())))
val graphQL = GraphQL.newGraphQL(schema)
    .instrumentation(FederatedTracingInstrumentation())
    .build()
```

### GraphQL Context Map

:::note
Default `GraphQLContextFactory` provided by `graphql-kotlin-spring-server` populates this header information automatically.
:::

Tracing header information can be provided by populating info directly on the GraphQL context map.

```kotlin
val contextMap = mutableMapOf<Any, Any>()
    .also { map ->
        request.headers().firstHeader(FEDERATED_TRACING_HEADER_NAME)?.let { headerValue ->
            map[FEDERATED_TRACING_HEADER_NAME] = headerValue
        }
    }

val executionInput = ExecutionInput.newExecutionInput()
    .graphQLContext(contextMap)
    .query(queryString)
    .build()
graphql.executeAsync(executionInput)
```

### `FederatedGraphQLContext` (Deprecated)

:::danger
Support for custom GraphQL context object is deprecated and will be removed in future releases. Please migrate to use
generic GraphQL context map.
:::

To best support tracing, the context must implement a specific method to get the HTTP headers from the request.
This is done by implementing the `FederatedGraphQLContext` interface instead of just the `GraphQLContext` interface
from `graphql-kotlin-schema-generator`.

```kotlin
class MyFederatedGraphQLContext(private val request: ServerRequest) : FederatedGraphQLContext {
    override fun getHTTPRequestHeader(caseInsensitiveHeaderName: String): String? =
        request.headers().firstHeader(caseInsensitiveHeaderName)
}

val executionInput = ExecutionInput.newExecutionInput()
    .context(MyFederatedGraphQLContext(httpRequest))
    .query(queryString)
    .build()
graphql.executeAsync(executionInput)
```
