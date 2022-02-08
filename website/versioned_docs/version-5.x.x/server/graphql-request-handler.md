---
id: graphql-request-handler
title: GraphQLRequestHandler
---
The `GraphQLRequestHandler` is an open and extendable class that contains the basic logic to get a `GraphQLResponse`.

It requires a `GraphQLSchema` and a [DataLoaderRegistryFactory](data-loaders.md) in the constructor.
For each request, it accepts a `GraphQLRequest` and an optional [GraphQLContext](./graphql-context-factory.md), and calls the `DataLoaderRegistryFactory` to generate a new `DataLoaderRegistry`.
Then all of these objects are sent to the schema for execution and the result is mapped to a `GraphQLResponse`.

There shouldn't be much need to change this class but if you wanted to add custom logic or logging it is possible to override it or just create your own.
