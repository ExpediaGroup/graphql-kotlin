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

## Query Handler

The library uses the [QueryHandler](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/QueryHandler.kt) interface to execute the `GraphQLRequest`. While you do not have access to the HTTP information here, you can modify the [default query handler bean](./spring-beans.md), `SimpleQueryHandler` and create the request with custom logic.
