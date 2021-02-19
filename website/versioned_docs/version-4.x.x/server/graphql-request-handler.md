---
id: graphql-request-handler
title: GraphQLRequestHandler
original_id: graphql-request-handler
---
The `GraphQLRequestHandler` is an open and extendable class that contains the basic logic to get a `GraphQLResponse` from `graphql-kotlin-types`.
It accepts a `GraphQLRequest`, an optional [GraphQLContext](./graphql-context-factory.md) and sends that to the GraphQL schema along with the [DataLoaderRegistry](data-loaders.md).

There shouldn't be much need to change this class but if you wanted to add custom logic or logging it is possible to override it or just create your own.
