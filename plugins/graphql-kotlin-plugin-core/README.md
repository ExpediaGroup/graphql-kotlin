# GraphQL Kotlin Plugin Core
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-plugin-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-plugin-core%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-plugin-core.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-plugin-core)

Module containing common logic used by the GraphQL Kotlin Gradle and Maven plugins. This module is not intended to be
consumed directly and instead you should rely on the provided build plugins.

Current functionality
* download SDL from a target endpoint
* run introspection query against target endpoint and return underlying schema
* generate GraphQL Kotlin client code from the specified queries to be run against target GraphQL schema. Code is generated
using [square/kotlinpoet](https://github.com/square/kotlinpoet) library.

### Code Generation Limitations

* Currently only Ktor Http Client is supported, additional clients (e.g. Spring WebClient) might be supported in the future.
* Due to the custom logic required for deserialization of polymorphic types and default enum values only Jackson is currently supported.
* Only single operation per query file is supported.
* Subscriptions are currently not supported.
* Within single GraphQL query you cannot make multiple selections to the same GraphQL object with different fields but it
is perfectly fine to have different selection sets across different GraphQL queries.
* Anonymous operations are supported but will result in generic `AnonymousQuery` (for query operation) class. Plugins
do not keep track of state across different query generations so if you have multiple anonymous operations in a single
package your compilation will fail due to the generic class name collisions.
