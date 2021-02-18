---
id: http-request-response
title: Access the HTTP Request-Response
original_id: http-request-response
---

GraphQL is strongly typed and any data that is not part of the schema is no longer automatically known by the clients. Relying on this information becomes an "undocumented" part of your API. As a result, by default, GraphQL query resolvers do not have access to the raw HTTP request and response objects.

That being said, there are some common use cases (like authorization) that require inspecting HTTP headers.

## GraphQL Context

The most common way to access the raw HTTP request and response objects is to process them when creating the GraphQLContext through the Spring bean [GraphQLContextFactory](./spring-graphql-context.md). Using the factory you can then extract the information from the incoming request and store it in the context so it can be accessed from any resolver.
