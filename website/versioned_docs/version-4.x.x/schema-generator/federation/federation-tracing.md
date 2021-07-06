---
id: federation-tracing
title: Federation Tracing
---

Support for Apollo Federation tracing is added to the `graphql-kotlin-federation` package by using the [apollographql/federation-jvm](https://github.com/apollographql/federation-jvm) library.

### `FederatedGraphQLContext`

To best support tracing, the context must implement a specific method to get the HTTP headers from the request.
This is done by implementing the `FederatedGraphQLContext` interface instead of just the `GraphQLContext` interface from `graphql-kotlin-schema-generator`.
