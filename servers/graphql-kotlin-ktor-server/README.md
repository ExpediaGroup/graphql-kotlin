# GraphQL Kotlin Ktor Server

[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-ktor-server.svg?label=Maven%20Central)](https://central.sonatype.com/search?namespace=com.expediagroup&q=name%3Agraphql-kotlin-ktor-server)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-ktor-server.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-ktor-server)

`graphql-kotlin-ktor-server` is a Ktor Server Plugin that simplifies setup of your GraphQL server..


## Installation

Using a JVM dependency manager, link `graphql-kotlin-ktor-server` to your project.

With Maven:

```xml
<dependency>
  <groupId>com.expediagroup</groupId>
  <artifactId>graphql-kotlin-ktor-server</artifactId>
  <version>${latestVersion}</version>
</dependency>
```

With Gradle:

```kotlin
implementation("com.expediagroup", "graphql-kotlin-ktor-server", latestVersion)
```

## Usage

`graphql-kotlin-ktor-server` is a Ktor Server Plugin and you to manually install it in your [module](https://ktor.io/docs/modules.html).

```kotlin
class HelloWorldQuery : Query {
    fun hello(): String = "Hello World!"
}

fun Application.graphQLModule() {
    install(GraphQL) {
        schema {
            packages = listOf("com.example")
            queries = listOf(
                HelloWorldQuery()
            )
        }
    }
    routing {
        graphQLPostRoute()
    }
}
```

If you use `EngineMain` to start your Ktor server, you can specify your module configuration in your `application.conf` (default)
or `application.yaml` (requires additional `ktor-server-config-yaml` dependency) file.

```
ktor {
    application {
        modules = [ com.example.ApplicationKt.graphQLModule ]
    }
}
```

## Documentation

Additional information can be found in our [documentation](https://opensource.expediagroup.com/graphql-kotlin/docs/server/ktor-server/ktor-overview)
and the [Javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-ktor-server) of all published library versions.

If you have a question about something you can not find in our documentation or javadocs, feel free to [start a new discussion](https://github.com/ExpediaGroup/graphql-kotlin/discussions).
