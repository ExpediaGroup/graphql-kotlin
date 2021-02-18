---
id: client-customization
title: Client Customization
---
## Ktor HTTP Client Customization

`GraphQLKtorClient` is a thin wrapper on top of [Ktor HTTP Client](https://ktor.io/clients/index.html) and supports fully
asynchronous non-blocking communication. It is highly customizable and can be configured with any supported Ktor HTTP
[engine](https://ktor.io/clients/http-client/engines.html) and [features](https://ktor.io/clients/http-client/features.html).

`GraphQLKtorClient` uses the Ktor HTTP Client to execute the underlying queries. Clients can be customized with different
engines (defaults to Coroutine-based IO) and HTTP client features. Custom configurations can be applied through Ktor DSL
style builders.

See [Ktor HTTP Client documentation](https://ktor.io/clients/index.html) for additional details.

### Global Client Customization

A single instance of `GraphQLKtorClient` can be used to handle many GraphQL operations. You can specify a target engine factory and
configure it through the corresponding [HttpClientConfig](https://api.ktor.io/1.3.2/io.ktor.client/-http-client-config/index.html).
Ktor also provides a number of [standard HTTP features](https://ktor.io/clients/http-client/features.html) and
allows you to easily create custom ones that can be configured globally.

The below example configures a new `GraphQLKtorClient` to use the `OkHttp` engine with custom timeouts, adds a default `X-MY-API-KEY`
header to all requests, and enables basic logging of the requests.

```kotlin

val client = GraphQLKtorClient(
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

val helloWorldQuery = HelloWorldQuery(variables = HelloWorldQuery.Variables(name = "John Doe"))
val result = client.execute(helloWorldQuery) {
    header("X-B3-TraceId", "0123456789abcdef")
}

```

## Spring WebClient Customization

`GraphQLWebClient` is a thin wrapper on top of [Spring WebClient](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html)
that relies on Reactor Netty for fully asynchronous non-blocking communications. If you want to use Jetty instead you will
need to exclude provided `io.projectreactor.netty:reactor-netty` dependency and instead add `org.eclipse.jetty:jetty-reactive-httpclient`
dependency.

### Global Client Customization

A single instance of `GraphQLWebClient` can be used to handle many GraphQL operations and you can customize it by providing
a custom instance of `WebClient.Builder`. See [Spring documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-webclient-customization)
for additional details.

Example below configures `GraphQLWebClient` with custom timeouts and adds a default `X-MY-API-KEY` header to all requests.

```kotlin

val httpClient: HttpClient = HttpClient.create()
    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
    .responseTimeout(Duration.ofMillis(10_000))
val connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient.wiretap(true))
val webClientBuilder = WebClient.builder()
    .clientConnector(connector)
    .defaultHeader("X-MY-API-KEY", "someSecretApiKey")

val client = GraphQLWebClient(
    url = "http://localhost:8080/graphql",
    builder = webClientBuilder
)

```

### Per Request Customization

Individual GraphQL requests can be customized by providing `WebClient.RequestBodyUriSpec` lambda. You can use this mechanism
to specify custom headers or include custom attributes or query parameters.

```kotlin

val helloWorldQuery = HelloWorldQuery(variables = HelloWorldQuery.Variables(name = "John Doe"))
val result = client.execute(helloWorldQuery) {
    header("X-B3-TraceId", "0123456789abcdef")
}

```

## Custom GraphQL Client

GraphQL Kotlin libraries provide generic a `GraphQLClient` interface as well as Ktor HTTP Client and Spring WebClient based
reference implementations. Both `GraphQLKtorClient` and `GraphQLWebClient` are open classes which means you can also
extend them to provide some custom `execute` logic.

```kotlin

class CustomGraphQLClient(url: URL) : GraphQLKtorClient<CIOEngineConfig>(url = url, engineFactory = CIO) {

    override suspend fun <T> execute(request: GraphQLClientRequest, requestCustomizer: HttpRequestBuilder.() -> Unit): GraphQLResponse<T> {
        // custom init logic
        val result = super.execute(request, requestCustomizer)
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

## Deprecated Field Usage

Build plugins will automatically fail generation of a client if any of the specified query files are referencing
deprecated fields. This ensures that your clients have to explicitly opt-in into deprecated usage by specifying
`allowDeprecatedFields` configuration option.

## Custom GraphQL Scalars

By default, custom GraphQL scalars are serialized and [type-aliased](https://kotlinlang.org/docs/reference/type-aliases.html)
to a String. GraphQL Kotlin plugins also support custom serialization based on provided configuration.

In order to automatically convert between custom GraphQL `UUID` scalar type and `java.util.UUID`, we first need to create
our custom `ScalarConverter`.

```kotlin

package com.example.client

import com.expediagroup.graphql.client.converter.ScalarConverter
import java.util.UUID

class UUIDScalarConverter : ScalarConverter<UUID> {
    override fun toScalar(rawValue: Any): UUID = UUID.fromString(rawValue.toString())
    override fun toJson(value: UUID): Any = value.toString()
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
    customScalars = listOf(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))
}

```

See [Gradle](../plugins/gradle-plugin-tasks.mdx) and [Maven](../plugins/maven-plugin-goals.md) plugin documentation for additional details.
