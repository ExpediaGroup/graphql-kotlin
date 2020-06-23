---
id: http-request-response
title: Access the HTTP Request-Response
---

Sometimes you may need access to the raw HTTP request and response object.
There are multiple ways and locations you can access this information, however note that GraphQL has different paradigms than traditional REST APIs.
Any data that is not part of the schema is no longer known by clients for free and it makes communication more difficult as this becomes an "undocumented" part of your API.

That being said, there are common use cases like authentication and authorization that require inspecting HTTP headers.

## GraphQL Context

Using the [GraphQLContextFactory](./spring-graphql-context.md) you can access the request and response and add information into the GraphQL context on every operation which can then be used in your schema functions.
