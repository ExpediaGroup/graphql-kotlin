# GraphQL Kotlin Gradle Plugin

[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-gradle-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-gradle-plugin%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-gradle-plugin.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-gradle-plugin)
[![Plugin Portal](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-gradle-plugin.svg?label=Plugin%20Portal)](https://plugins.gradle.org/plugin/com.expediagroup.graphql)

GraphQL gradle plugin provides functionality to introspect GraphQL schemas and generate a lightweight GraphQL HTTP client.

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
`graphql`. Additional information on the available tasks and their available configuration options can be found on our
[documentation pages](https://expediagroup.github.io/graphql-kotlin/docs/plugins/gradle-plugin).

### graphqlIntrospectSchema

Task that executes GraphQL introspection query against specified `endpoint` and saves the underlying schema file as
`schema.graphql` under build directory. In general, this task provides limited functionality by itself and instead
should be used to generate input for the subsequent `graphqlGenerateClient` task.

## Documentation

Additional information can be found in our [documentation](https://expediagroup.github.io/graphql-kotlin/docs/plugins/gradle-plugin)
and the [Javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-gradle-plugin) of all published versions.

If you have a question about something you can not find in our documentation or Javadocs, feel free to
[create an issue](https://github.com/ExpediaGroup/graphql-kotlin/issues) and tag it with the question label.
