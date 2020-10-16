---
id: client-overview
title: Client Overview
---

GraphQL Kotlin provides a set of lightweight type-safe GraphQL HTTP clients. The library provides [Ktor HTTP client](https://ktor.io/clients/index.html)
and [Spring WebClient](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-webclient)
based reference implementations as well as allows for custom implementations using other engines, see [client customization](client-customization.md)
documentation for additional details. Type-safe data models are generated at build time by the GraphQL Kotlin [Gradle](../plugins/gradle-plugin.md)
and [Maven](../plugins/maven-plugin.md) plugins.

Client Features:
* Supports query and mutation operations
* Automatic generation of type-safe Kotlin models
* Custom scalar support - defaults to String but can be configured to deserialize to specific types
* Supports default enum values to gracefully handle new/unknown server values
* Native support for coroutines
* Easily configurable Ktor and Spring WebClient based HTTP Clients
* Documentation generated from the underlying GraphQL schema

## Project Configuration

GraphQL Kotlin provides both Gradle and Maven plugins to automatically generate your client code at build time. In order
to auto-generate the client code, plugins require target GraphQL schema and a list of query files to process.

GraphQL schema can be provided as

* result of introspection query (default)
* downloaded from an SDL endpoint
* local file

See [Gradle](https://expediagroup.github.io/graphql-kotlin/docs/plugins/gradle-plugin) and [Maven](https://expediagroup.github.io/graphql-kotlin/docs/plugins/maven-plugin)
plugin documentation for additional details.

Once your data classes are generated, you can then execute their underlying GraphQL operations using one of the provided
clients. By default, generated client code will rely on the generic interface which means that you will need to either
implement a custom client OR pull in one of the reference implementations.

> NOTE: If you are going to be using one of the reference implementations it is highly recommended to also configure the
plugin to generate client type specific code as otherwise you will have limited customization options. See [client customization](client-customization.md)
for additional details.

Example below configures the project to use introspection query to obtain the schema and uses Ktor based HTTP client.

### Build Configuration

<!--DOCUSAURUS_CODE_TABS-->
<!--Gradle-->

Basic `build.gradle.kts` Gradle configuration that executes introspection query against specified endpoint to obtain target
schema and then generate the clients under `com.example.generated` package name:

```kotlin
import com.expediagroup.graphql.plugin.generator.GraphQLClientType
import com.expediagroup.graphql.plugin.gradle.graphql

plugins {
    id("com.expediagroup.graphql") version $latestGraphQLKotlinVersion
}

dependencies {
  implementation("com.expediagroup:graphql-kotlin-ktor-client:$latestGraphQLKotlinVersion")
}

graphql {
    client {
        endpoint = "http://localhost:8080/graphql"
        packageName = "com.example.generated"
        clientType = GraphQLClientType.KTOR
    }
}
```

<!--Maven-->

Basic Maven `pom.xml` configuration that executes introspection query against specified endpoint to obtain target
schema and then generate the clients under `com.example.generated` package name:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>graphql-kotlin-maven-client-example</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <properties>
        <graphql-kotlin.version>$latestGraphQLKotlinVersion</graphql-kotlin.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.expediagroup</groupId>
            <artifactId>graphql-kotlin-ktor-client</artifactId>
            <version>${graphql-kotlin.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>com.expediagroup</groupId>
                <artifactId>graphql-kotlin-maven-plugin</artifactId>
                <version>${graphql-kotlin.version}</version>
                <executions>
                    <execution>
                        <id>generate-graphql-client</id>
                        <goals>
                            <goal>introspectSchema</goal>
                            <goal>generateClient</goal>
                        </goals>
                        <configuration>
                            <endpoint>http://localhost:8080/graphql</endpoint>
                            <packageName>com.example.generated</packageName>
                            <schemaFile>${project.build.directory}/schema.graphql</schemaFile>
                            <clientType>KTOR</clientType>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

<!--END_DOCUSAURUS_CODE_TABS-->

See [graphql-kotlin-client-example](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/client) project for complete
working examples of Gradle and Maven based projects.

### Generating GraphQL Client

By default, GraphQL Kotlin build plugins will attempt to generate GraphQL clients from all `*.graphql` files located under
`src/main/resources`. Queries are validated against the target GraphQL schema, which can be manually provided, retrieved by
the plugins through introspection (as configured in examples above) or downloaded directly from a custom SDL endpoint.
See our documentation for more details on supported [Gradle tasks](../plugins/gradle-plugin.md)
and [Maven Mojos](../plugins/maven-plugin.md).

When creating your GraphQL queries make sure to always specify an operation name and name the files accordingly. Each
one of your query files will generate a corresponding Kotlin file with a class matching your operation
name that will act as a wrapper for all corresponding data classes. For example, given `HelloWorldQuery.graphql` with
`HelloWorldQuery` as the operation name, GraphQL Kotlin plugins will generate a corresponding `HelloWorldQuery.kt` file
with a `HelloWorldQuery` class under the configured package.

For example, given a simple schema

```graphql
type Query {
  helloWorld: String
}
```

And a corresponding `HelloWorldQuery.graphql` query

```graphql
query HelloWorldQuery {
  helloWorld
}
```

Plugins will generate following client code

```kotlin
package com.example.generated

import com.expediagroup.graphql.client.GraphQLKtorClient
import com.expediagroup.graphql.types.GraphQLResponse
import kotlin.String

const val HELLO_WORLD_QUERY: String = "query HelloWorldQuery {\n    helloWorld\n}"

class HelloWorldQuery(
  private val graphQLClient: GraphQLKtorClient<*>
) {
  suspend fun execute(requestBuilder: HttpRequestBuilder.() -> Unit = {}): GraphQLResponse<HelloWorldQuery.Result> =
      graphQLClient.execute(HELLO_WORLD_QUERY, "HelloWorldQuery", null, requestBuilder)

  data class Result(
    val helloWorld: String
  )
}
```

Generated classes requires an instance of `GraphQLKtorClient` and exposes a single `execute` suspendable method that executes
the underlying GraphQL operation using the provided client.

### Executing Queries

Your auto generated classes accept an instance of `GraphQLKtorClient` which is a thin wrapper around Ktor HTTP client that
ensures proper serialization and deserialization of your GraphQL objects. `GraphQLKtorClient` requires target URL to be
specified and defaults to fully asynchronous non-blocking [Coroutine-based IO engine](https://ktor.io/clients/http-client/engines.html#cio).

```kotlin
package com.example.client

import com.expediagroup.graphql.client.GraphQLKtorClient
import com.expediagroup.graphql.generated.HelloWorldQuery
import kotlinx.coroutines.runBlocking
import java.net.URL

fun main() {
    val client = GraphQLKtorClient(url = URL("http://localhost:8080/graphql"))
    val helloWorldQuery = HelloWorldQuery(client)
    runBlocking {
        val result = helloWorldQuery.execute()
        println("hello world query result: ${result.data?.helloWorld}")
    }
    client.close()
}
```
