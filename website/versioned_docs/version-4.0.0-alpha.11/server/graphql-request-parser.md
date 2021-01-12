---
id: version-4.0.0-alpha.11-graphql-request-parser
title: GraphQLRequestParser
original_id: graphql-request-parser
---

The `GraphQLRequestParser` interface is requrired to parse the library-specific HTTP request object into the common `GraphQLRequest` class from `graphql-kotlin-types`.

```kotlin
interface GraphQLRequestParser<Request> {
    suspend fun parseRequest(request: Request): GraphQLRequest?
}
```

While not offically part of the spec, there is a standard format used by most GraphQL clients and servers for [serving GraphQL over HTTP](https://graphql.org/learn/serving-over-http/).

If the request is not a valid GraphQL format, the interface should return `null` and let the server specific code return a bad request status to the client.
This is not the same as a GraphQL error or an exception thrown by the schema.
Those types of errors should still parse the request and return a valid response with errors set via the [GraphQLRequestHandler](./graphql-request-handler.md).

This interface should only be concerned with parsing the request, not about forwarding info to the context or execution.
That is handled by the [GraphQLContextFactory](./graphql-context-factory.md).
