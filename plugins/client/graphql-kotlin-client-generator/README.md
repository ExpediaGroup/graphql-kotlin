# GraphQL Kotlin Client Generator
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-client-generator.svg?label=Maven%20Central)](https://central.sonatype.com/search?namespace=com.expediagroup&q=name%3Agraphql-kotlin-client-generator)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-client-generator.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-client-generator)

Module containing common logic used by the GraphQL Kotlin Gradle and Maven plugins to generate the client code. **This
module is not intended to be consumed directly. Instead, you should rely on the functionality provided by the build plugins.**

## Common Functionality

* downloadSDL

GraphQL endpoints are often public and as such many servers might disable introspection queries in production environment.
Since GraphQL schema is needed to generate type safe clients, as alternative GraphQL servers might expose private
endpoints (e.g. accessible only from within network, etc) that could be used to download schema in Schema Definition
Language (SDL) directly. This function will attempt to download schema SDL from the specified endpoint, verify it is a
valid SDL and return it to the client.

* introspectSchema

Executes introspection query against GraphQL endpoint, constructs GraphQL schema from the results and returns pretty
print representation of the resulting schema.

* generateClient

Generate GraphQL Kotlin client code from the specified queries to be run against target GraphQL schema. Code is generated
using [square/kotlinpoet](https://github.com/square/kotlinpoet) library.

## Code Generation Limitations

* Only a single operation per GraphQL query file is supported. To avoid duplication of return types, a shared fragments file can be used.
* Subscriptions are currently NOT supported.
