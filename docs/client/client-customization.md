---
id: client-customization
title: Client Customization
---

## Ktor HTTP Client Customization

`GraphQLClient` uses the Ktor HTTP Client to execute the underlying queries. Clients can be customized with different
engines (defaults to Coroutine-based IO) and HTTP client features. Custom configurations can be applied through Ktor DSL
style builders.

```kotlin
val client = GraphQLClient(
        url = URL("http://localhost:8080/graphql"),
        engineFactory = OkHttp
) {
    engine {
        config {
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
        }
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.HEADERS
    }
}
```

See [Ktor HTTP Client documentation](https://ktor.io/clients/index.html) for additional details.

## Jackson Customization

`GraphQLClient` relies on Jackson to handle polymorphic types and default enum values. Due to the necessary logic to
handle the above, currently we don't support other JSON libraries.

```kotlin
val customObjectMapper = jacksonObjectMapper()
val client = GraphQLClient(url = URL("http://localhost:8080/graphql"), mapper = customObjectMapper)
```

## Deprecated Field Usage

Build plugins will automatically fail generation of a client if any of the specified query files are referencing
deprecated fields. This ensures that your clients have to explicitly opt-in into deprecated usage by specifying
`allowDeprecatedFields` configuration option.

## Custom GraphQL Scalars

By default, custom GraphQL scalars are serialized and [type-aliased](https://kotlinlang.org/docs/reference/type-aliases.html)
to a String. GraphQL Kotlin plugins also support custom serialization based on provided configuration.

In order to automatically convert between custom GraphQL `UUID` scalar type and `java.util.UUID`, we first need to create
our custom `ScalarConverter`.

```kotlin
package com.example.client

import com.expediagroup.graphql.client.converter.ScalarConverter
import java.util.UUID

class UUIDScalarConverter : ScalarConverter<UUID> {
    override fun toScalar(rawValue: String): UUID = UUID.fromString(rawValue)
    override fun toJson(value: UUID): String = value.toString()
}
```

And then configure build plugin by specifying
* custom GraphQL scalar name
* target class name
* converter that provides logic to map between GraphQL and Kotlin type

```kotlin
graphql {
    packageName = "com.example.generated"
    endpoint = "http://localhost:8080/graphql"
    converters.put("UUID", ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter"))
}
```

See [Gradle](https://expediagroup.github.io/graphql-kotlin/docs/plugins/gradle-plugin#generating-client-with-custom-scalars)
and [Maven](https://expediagroup.github.io/graphql-kotlin/docs/plugins/maven-plugin#generating-client-with-custom-scalars)
plugin documentation for additional details.
