---
id: gradle-plugin
title: GraphQL Kotlin Gradle Plugin
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

### graphqlIntrospectSchema

Executes GraphQL introspection query against specified endpoint and saves the underlying schema file as
`schema.graphql` under build directory. In general, this task provides limited functionality by itself and instead
should be used to generate input for the subsequent `graphqlGenerateClient` task.

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

NOTE: This task does not automatically configure itself to be part of your build lifecycle and you need to explicitly
invoke it OR configure it as a dependency of some other task.

## Complete Example

// TODO complete once all tasks are merged
