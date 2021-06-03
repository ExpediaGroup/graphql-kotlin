---
id: client-overview
title: Client Overview
original_id: client-overview
---
`graphql-kotlin-client` is a lightweight type-safe GraphQL HTTP client. Type-safe data models are generated at build time
by the GraphQL Kotlin [Gradle](../plugins/gradle-plugin.md) and
[Maven](../plugins/maven-plugin.md) plugins.

`GraphQLClient` is a thin wrapper on top of [Ktor HTTP Client](https://ktor.io/clients/index.html) and supports fully
asynchronous non-blocking communication. It is highly customizable and can be configured with any supported Ktor HTTP
[engine](https://ktor.io/clients/http-client/engines.html) and [features](https://ktor.io/clients/http-client/features.html).

## Project Configuration

GraphQL Kotlin provides both Gradle and Maven plugins to automatically generate your client code at build time. Once
your data classes are generated, you can then execute their underlying GraphQL operations using `graphql-kotlin-client`
runtime dependency.

Basic `build.gradle.kts` Gradle configuration:

```kotlin

import com.expediagroup.graphql.plugin.gradle.graphql

plugins {
    id("com.expediagroup.graphql") version $latestGraphQLKotlinVersion
}

dependencies {
  implementation("com.expediagroup:graphql-kotlin-client:$latestGraphQLKotlinVersion")
}

graphql {
    client {
        endpoint = "http://localhost:8080/graphql"
        packageName = "com.example.generated"
    }
}

```

Equivalent `pom.xml` Maven configuration

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
            <artifactId>graphql-kotlin-client</artifactId>
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
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>

```

See [graphql-kotlin-client-example](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/examples/client) project for complete
working examples of Gradle and Maven based projects.

## Generating GraphQL Client

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

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.types.GraphQLResponse
import kotlin.String

const val HELLO_WORLD_QUERY: String = "query HelloWorldQuery {\n    helloWorld\n}"

class HelloWorldQuery(
  private val graphQLClient: GraphQLClient
) {
  suspend fun execute(): GraphQLResponse<HelloWorldQuery.Result> =
      graphQLClient.execute(HELLO_WORLD_QUERY, "HelloWorldQuery", null)

  data class Result(
    val helloWorld: String
  )
}

```

Generated classes requires an instance of `GraphQLClient` and exposes a single `execute` suspendable method that executes
the underlying GraphQL operation using the provided client.

## Executing Queries

Your auto generated classes accept an instance of `GraphQLClient` which is a thin wrapper around Ktor HTTP client that
ensures proper serialization and deserialization of your GraphQL objects. `GraphQLClient` requires target URL to be
specified and defaults to fully asynchronous non-blocking [Coroutine-based IO engine](https://ktor.io/clients/http-client/engines.html#cio).

```kotlin

package com.example.client

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.generated.HelloWorldQuery
import kotlinx.coroutines.runBlocking
import java.net.URL

fun main() {
    val client = GraphQLClient(url = URL("http://localhost:8080/graphql"))
    val helloWorldQuery = HelloWorldQuery(client)
    runBlocking {
        val result = helloWorldQuery.execute()
        println("hello world query result: ${result.data?.helloWorld}")
    }
    client.close()
}

```
