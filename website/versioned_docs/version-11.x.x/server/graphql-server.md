---
id: graphql-server
title: GraphQLServer
---
`graphql-kotlin-server` provides common code and basic interfaces to setup a GraphQL server in any framework.

The official reference implementations are:

- [graphql-kotlin-spring-server](./spring-server/spring-overview.mdx)

We recommend using one of the implementations as the common code has very little logic but you can still use the common
package to create implementation for other libraries (Ktor, Spark, etc).

There are demos of how to use these server libraries in the `/examples` folder of the repo.

## `GraphQLServer`

The top level object in the common package is `GraphQLServer<T>`.
This class is open for extensions and requires that you specify the type of the http requests you will be handling.

-   For [Spring Reactive](https://spring.io/reactive) we would define a `GraphQLServer<ServerRequest>`
-   For [Ktor](https://ktor.io/) we would define a `GraphQLServer<ApplicationRequest>`

In its simplest form, a GraphQL server has the following responsibilties:

-   Parse the GraphQL request info from the HTTP request
-   Create a `GraphQLContext` object from the HTTP request to be used during execution
-   Send the request and the context to the GraphQL schema to execute and get a response (may contain `data` or `errors`)
-   Send the reponse back to the client over HTTP

Most of the logic in a GraphQL server that is specific to your application is already in the schema, so if we have interfaces for all these
common functions, we can abstract away the library specific features.

The one method we don't have an interface for is sending back the response to the client. Once you get the response back from `GraphQLServer`,
we leave the rest up to your application to call it's server specific methods to encode and send the response.
