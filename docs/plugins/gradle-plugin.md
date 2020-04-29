---
id: gradle-plugin
title: Gradle Plugin
---

GraphQL Kotlin Gradle Plugin provides functionality to introspect GraphQL schemas and generate a lightweight GraphQL HTTP client.

## Usage

`graphql-kotlin-gradle-plugin` is published on Gradle [Plugin Portal](https://plugins.gradle.org/plugin/com.expediagroup.graphql).
In order to execute any of the provided tasks you need to first apply the plugin on your project.

```kotlin
// build.gradle.kts
plugins {
    id("com.expediagroup.graphql") version $graphQLKotlinVersion
}
```

GraphQL Kotlin Gradle Plugin uses an extension on the project named `graphql` of type
[GraphQLPluginExtension`](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/plugins/graphql-kotlin-gradle-plugin/src/main/kotlin/com/expediagroup/graphql/plugin/gradle/GraphQLPluginExtension.kt).
This extension can be used to configure global options instead of explicitly configuring individual tasks.

## Tasks

All `graphql-kotlin-gradle-plugin` tasks are grouped together under `GraphQL` task group and their names are prefixed with
`graphql`. You can find detailed information about GraphQL kotlin tasks by running Gradle `help --task <taskName>` task.

### graphqlDownloadSDL

Task that attempts to download GraphQL schema in SDL format from the specified `endpoint` and saves the underlying
schema file as `schema.graphql` under build directory. In general, this task provides limited functionality by itself
and could be used as an alternative to `graphqlIntrospectSchema` to generate input for the subsequent
`graphqlGenerateClient` task.

### graphqlIntrospectSchema

Executes GraphQL introspection query against specified endpoint and saves the underlying schema file as
`schema.graphql` under build directory. In general, this task provides limited functionality by itself and instead
should be used to generate input for the subsequent `graphqlGenerateClient` task.

## Downloading Schema SDL

GraphQL endpoints are often public and as such many servers might disable introspection queries in production environment.
Since GraphQL schema is needed to generate type safe clients, as alternative GraphQL servers might expose private
endpoints (e.g. accessible only from within network, etc) that could be used to download schema in Schema Definition
Language (SDL) directly. `graphqlDownloadSDL` task requires target GraphQL server `endpoint` to be specified and can
be executed directly from the command line by explicitly passing `endpoint` parameter

```shell script
$ gradle graphqlDownloadSDL --endpoint="http://localhost:8080/sdl"
```

Task can also be configured in your Gradle build file using global extension

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.graphql

graphql {
  sdlEndpoint = "http://localhost:8080/graphql"
}
```

Or by explicitly configuring the task

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask

val graphqlIntrospectSchema by tasks.getting(GraphQLDownloadSDLTask::class) {
    graphqlDownloadSDL.set("http://localhost:8080/graphql")
}
```

NOTE: This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly
invoke it OR configure it as a dependency of some other task.

## Introspecting Schema

Introspection task requires target GraphQL server `endpoint` to be specified. Task can be executed directly from the
command line by explicitly passing endpoint parameter

```shell script
$ gradle graphqlIntrospectSchema --endpoint="http://localhost:8080/graphql"
```

Task can also be configured in your Gradle build file using global extension

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.graphql

graphql {
  endpoint = "http://localhost:8080/graphql"
}
```

Or by explicitly configuring the task

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {
    endpoint.set("http://localhost:8080/graphql")
}
```

NOTE: This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly
invoke it OR configure it as a dependency of some other task.

## Complete Example

// TODO complete once all tasks are merged
