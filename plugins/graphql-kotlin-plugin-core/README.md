# GraphQL Kotlin Plugin Core
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-plugin-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-plugin-core%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-plugin-core.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-plugin-core)

Module containing common logic used by the GraphQL Kotlin Gradle and Maven plugins. **This module is not intended to be
consumed directly and instead you should rely on the provided build plugins.**

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
