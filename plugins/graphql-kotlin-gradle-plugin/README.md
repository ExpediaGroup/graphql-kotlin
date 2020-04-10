# GraphQL Kotlin Gradle Plugin

[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-gradle-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-gradle-plugin%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-gradle-plugin.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-gradle-plugin)
[![Plugin Portal](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-gradle-plugin.svg?label=Plugin%20Portal)](https://plugins.gradle.org/plugin/com.expediagroup.graphql)

GraphQL gradle plugin provides functionality to introspect GraphQL schemas and generate a lightweight GraphQL HTTP client.

## Applying Plugin

`graphql-kotlin-gradle-plugin` is published on Gradle [Plugin Portal](https://plugins.gradle.org/plugin/com.expediagroup.graphql).
In order to execute any of the provided tasks you need to first apply the plugin on your project and configure necessary
runtime dependencies.

```kotlin
// build.gradle.kts
plugins {
    id("com.expediagroup.graphql") version $graphQLKotlinVersion
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-client:$graphQLKotlinVersion")
}
```

## Extension

The simplest way to generate GraphQL client is to configure the `graphql` project extension with target `packageName`
for the generated classes and GraphQL server `endpoint` that will be used to run introspection query to obtain the schema.
Extension will automatically configure the necessary tasks to download the schema through the introspection and then to
 generate the client code based on the queries located under project `src/main/resources` directory.

```kotlin
// build.gradle.kts
graphql {
    packageName = "your.package.name"
    endpoint = "http://localhost:8080/graphql"
}
```

Additional information on the `graphql` extension and available configuration options can be found on our [documentation pages](https://expediagroup.github.io/graphql-kotlin/).

## Tasks

All `graphql-kotlin-gradle-plugin` tasks are grouped together under `GraphQL` task group and their names are prefixed with
`graphql`. Additional information on the available tasks and their available configuration options can be found on our
[documentation pages](https://expediagroup.github.io/graphql-kotlin/).

### graphqlIntrospectSchema

Task that executes GraphQL introspection query against specified endpoint and saves the underlying schema file.

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {
    endpoint.set("<your /graphql endpoint>")
}
```

### graphqlDownloadSDL

Task that attempts to download GraphQL schema in SDL format from the specified endpoint and save it locally.

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask

val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
    endpoint.set("<your /graphql endpoint>")
}
```

### graphqlGenerateClient

Generate GraphQL Kotlin client and corresponding data classes based on the provided GraphQL queries.

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
    packageName.set("package.for.generated.classes")
    schemaFile.set(File("path/to/schema.graphql"))
}
```
