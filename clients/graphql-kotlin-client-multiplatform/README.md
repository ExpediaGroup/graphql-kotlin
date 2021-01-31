# GraphQL Kotlin Client Multiplatform
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-client.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-client-multiplatform%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-client.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-client-multiplatform)

This module defines an interface for a lightweight, typesafe GraphQL HTTP clients. See [graphql-kotlin-ktor-client-multiplatform](../graphql-kotlin-ktor-client-multiplatform) for reference implementations.

NOTE: GraphQL clients can be invoked directly by manually creating your corresponding data model but since at its core it
ends up with simple POST request this mode of operation is not that useful. Instead, GraphQL Kotlin clients should be used
together with one of the GraphQL Kotlin build plugins to auto-generate type safe data models based on the specified queries.

## Features

* Supports query and mutation operations
* Automatic generation of type-safe Kotlin models
* Custom scalar support - defaults to String but can be configured to deserialize to specific types
* Supports default enum values to gracefully handle new/unknown server values
* Native support for coroutines
* Easily configurable Ktor based HTTP Client
* Documentation generated from the underlying GraphQL schema
* Can be used in Kotlin multiplatform project (no platform-specific dependencies)

## Documentation

Additional information can be found in our [documentation](https://expediagroup.github.io/graphql-kotlin/docs/client/client-overview)
and the [Javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-client-multipatform) of all published versions.

If you have a question about something you can not find in our documentation or Javadocs, feel free to
[create an issue](https://github.com/ExpediaGroup/graphql-kotlin/issues) and tag it with the question label.
