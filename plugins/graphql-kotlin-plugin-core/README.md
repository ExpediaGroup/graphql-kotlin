# GraphQL Kotlin Plugin Core
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-plugin-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-plugin-core%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-plugin-core.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-plugin-core)

Module containing common logic used by the GraphQL Kotlin Gradle and Maven plugins. **This module is not intended to be
consumed directly and instead you should rely on the provided build plugins.**

## Common Functionality

* introspectSchema

Executes introspection query against GraphQL endpoint, constructs GraphQL schema from the results and returns pretty
print representation of the resulting schema.
