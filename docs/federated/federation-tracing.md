---
id: federation-tracing
title: Federation Tracing
---

Support for Apollo Federation tracing is added to both the `graphql-kotlin-federation` and `graphql-kotlin-spring-server` packages by using the [apollographql/federation-jvm](https://github.com/apollographql/federation-jvm) library.

### `FederationGraphQLContext`

To best support tracing the context must implement a specific method to get the HTTP headers from the context. This is done by implementing the `FederationGraphQLContext` interface instead of just the `GraphQLContext` interface from `graphql-kotlin`.

### `FederationGraphQLContextFactory`

To make sure we return the correct `GraphQLContext` implementation, add the `FederationGraphQLContextFactory` class as a bean instead of the regular `GraphQLContextFactory`.
This will then return a `FederationGraphQLContext` when we execute the different operations and will allow us to adding tracing information to the response.
