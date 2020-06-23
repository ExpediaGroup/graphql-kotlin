---
id: http-request-response
title: Access the HTTP Request-Response
---

GraphQL is strongly typed and any data that is not part of the schema is no longer automatically known by the clients. Relying on this information becomes an "undocumented" part of your API. As a result, by default, GraphQL query resolvers do not have access to the raw HTTP request and response objects.
There are multiple ways and locations you can access this information, however note that GraphQL has different paradigms than traditional REST APIs.
Any data that is not part of the schema is no longer known by clients for free and it makes communication more difficult as this becomes an "undocumented" part of your API.

That being said, there are some common use cases (like authorization) that require inspecting HTTP headers.

## GraphQL Context

Using the [GraphQLContextFactory](./spring-graphql-context.md) you can access the request and response and add information into the GraphQL context on every operation which can then be used in your schema functions.
