---
id: graphql-request-parser
title: GraphQLRequestParser
---
The `GraphQLRequestParser` interface is required to parse the library-specific HTTP request object into the common `GraphQLServerRequest` class.

```kotlin
interface GraphQLRequestParser<Request> {
    suspend fun parseRequest(request: Request): GraphQLServerRequest?
}
```

While not officially part of the spec, there is a standard format used by most GraphQL clients and servers for [serving GraphQL over HTTP](https://graphql.org/learn/serving-over-http/).
Following the above convention, GraphQL clients should generally use HTTP POST requests with the following body structure

```json
{
  "query": "...",
  "operationName": "...",
  "variables": { "myVariable": "someValue" }
}
```

where

- `query` is a required field and contains the operation (query, mutation, or subscription) to be executed
- `operationName` is an optional string, only required if multiple operations are specified in the `query` string.
- `variables` is an optional map of JSON objects that are referenced as input arguments in the `query` string

GraphQL Kotlin server supports both single and batch GraphQL requests. Batch requests are represented as a list of individual
GraphQL requests. When processing batch requests, same context will be used for processing all requests and server will respond
with a list of GraphQL responses.

If the request is not a valid GraphQL format, the interface should return `null` and let the server specific code return a bad request status to the client.
This is not the same as a GraphQL error or an exception thrown by the schema.
Those types of errors should still parse the request and return a valid response with errors set via the [GraphQLRequestHandler](./graphql-request-handler.md).

This interface should only be concerned with parsing the request, not about forwarding info to the context or execution.
That is handled by the [GraphQLContextFactory](./graphql-context-factory.md).
