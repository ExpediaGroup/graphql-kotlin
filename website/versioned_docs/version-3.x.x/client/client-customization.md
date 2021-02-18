---
id: client-customization
title: Client Customization
original_id: client-customization
---
## Ktor HTTP Client Customization

`GraphQLClient` uses the Ktor HTTP Client to execute the underlying queries. Clients can be customized with different
engines (defaults to Coroutine-based IO) and HTTP client features. Custom configurations can be applied through Ktor DSL
style builders.

See [Ktor HTTP Client documentation](https://ktor.io/clients/index.html) for additional details.

### Global Client Customization

A single instance of `GraphQLClient` can be used to power many GraphQL operations. You can specify a target engine factory and
configure it through the corresponding [HttpClientConfig](https://api.ktor.io/1.3.2/io.ktor.client/-http-client-config/index.html).
Ktor also provides a number of [standard HTTP features](https://ktor.io/clients/http-client/features.html) and
allows you to easily create custom ones that can be configured globally.

The below example configures a new `GraphQLClient` to use the `OkHttp` engine with custom timeouts, adds a default `X-MY-API-KEY`
header to all requests, and enables basic logging of the requests.

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
    defaultRequest {
        header("X-MY-API-KEY", "someSecretApiKey")
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.INFO
    }
}

```

### Per Request Customization

Individual GraphQL requests can be customized through [HttpRequestBuilder](https://api.ktor.io/1.3.2/io.ktor.client.request/-http-request-builder/).
You can use this mechanism to specify custom headers, update target url to include custom query parameters, configure
attributes that can be accessed from the pipeline features as well specify timeouts per request.

```kotlin

val helloWorldQuery = HelloWorldQuery(client)
val result = helloWorldQuery.execute(variables = HelloWorldQuery.Variables(name = null)) {
    header("X-B3-TraceId", "0123456789abcdef")
}

```

### Custom GraphQL client

`GraphQLClient` is an open class which means you can also extend it to provide custom `execute` logic.

```kotlin

class CustomGraphQLClient(url: URL) : GraphQLClient<CIOEngineConfig>(url = url, engineFactory = CIO) {

    override suspend fun <T> execute(query: String, operationName: String?, variables: Any?, resultType: Class<T>, requestBuilder: HttpRequestBuilder.() -> Unit): GraphQLResponse<T> {
        // custom init logic
        val result = super.execute(query, operationName, variables, resultType, requestBuilder)
        // custom finalize logic
        return result
    }
}

```

## Jackson Customization

`GraphQLClient` relies on Jackson to handle polymorphic types and default enum values. You can specify your own custom
object mapper configured with some additional serialization/deserialization features but due to the necessary logic to
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

-   Custom GraphQL scalar name
-   Target class name
-   Converter that provides logic to map between GraphQL and Kotlin type

```kotlin

graphql {
    packageName = "com.example.generated"
    endpoint = "http://localhost:8080/graphql"
    converters.put("UUID", ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter"))
}

```

See [Gradle](../plugins/gradle-plugin.md)
and [Maven](../plugins/maven-plugin.md)
plugin documentation for additional details.
