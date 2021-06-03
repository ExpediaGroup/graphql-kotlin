---
id: gradle-plugin
title: Gradle Plugin
original_id: gradle-plugin
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

## Extension

GraphQL Kotlin Gradle Plugin uses an extension on the project named `graphql` of type
[GraphQLPluginExtension](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/plugins/graphql-kotlin-gradle-plugin/src/main/kotlin/com/expediagroup/graphql/plugin/gradle/GraphQLPluginExtension.kt).
This extension can be used to configure global options instead of explicitly configuring individual tasks. Once extension
is configured, it will automatically download SDL/run introspection to generate GraphQL schema and subsequently generate
all GraphQL clients. GraphQL Extension should be used by default, except for cases where you need to only run individual
tasks.

```kotlin

// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.graphql

graphql {
  client {
    // GraphQL server endpoint that will be used to for running introspection queries. Alternatively you can download schema directly from `sdlEndpoint`.
    endpoint = "http://localhost:8080/graphql"
    // GraphQL server SDL endpoint that will be used to download schema. Alternatively you can run introspection query against `endpoint`.
    sdlEndpoint = "http://localhost:8080/sdl"
    // Target package name to be used for generated classes.
    packageName = "com.example.generated"
    // Optional HTTP headers to be specified on an introspection query or SDL request.
    headers["X-Custom-Header"] = "Custom-Header-Value"
    // Boolean flag indicating whether or not selection of deprecated fields is allowed.
    allowDeprecatedFields = false
    // Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.
    converters["UUID"] = ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter")
    // List of query files to be processed.
    queryFiles.add(file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql"))
    // Timeout configuration
    timeout {
        // Connect timeout in milliseconds
        connect = 5_000
        // Read timeout in milliseconds
        read = 15_000
    }
  }
}

```

## Tasks

All `graphql-kotlin-gradle-plugin` tasks are grouped together under `GraphQL` task group and their names are prefixed with
`graphql`. You can find detailed information about GraphQL kotlin tasks by running Gradle `help --task <taskName>` task.

### graphqlDownloadSDL

Task that attempts to download GraphQL schema in SDL format from the specified `endpoint` and saves the underlying
schema file as `schema.graphql` under build directory. In general, this task provides limited functionality by itself
and could be used as an alternative to `graphqlIntrospectSchema` to generate input for the subsequent
`graphqlGenerateClient` task.

**Properties**

| Property               | Type                | Required | Description                                                                                                                                                                               |
| ---------------------- | ------------------- | -------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `endpoint`      | String              | yes      | Target GraphQL server SDL endpoint that will be used to download schema.<br />**Command line property is**: `endpoint`.                                                             |
| `headers`       | Map&lt;String, Any&gt; |          | Optional HTTP headers to be specified on a SDL request.                                                                                                                                   |
| `timeoutConfig` | TimeoutConfig       |          | Timeout configuration(in milliseconds) to download schema from SDL endpoint before we cancel the request.<br />**Default value are:** connect timeout = 5_000, read timeout = 15_000.<br /> |

### graphqlGenerateClient

Task that generates GraphQL Kotlin client and corresponding data classes based on the provided GraphQL queries that are
evaluated against target Graphql schema. Individual clients with their specific data models are generated for each query
file and are placed under specified `packageName`. When this task is added to the project, either through explicit configuration
or through the `graphql` extension, it will automatically configure itself as a dependency of a `compileKotlin` task and
resulting generated code will be automatically added to the project main source set.

**Properties**

| Property                       | Type                            | Required                                                          | Description                                                                                                                                                                                                                                                                               |
| ------------------------------ | ------------------------------- | ----------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `allowDeprecatedFields` | Boolean                         |                                                                   | Boolean flag indicating whether selection of deprecated fields is allowed or not.<br />**Default value is:** `false`.<br />**Command line property is**: `allowDeprecatedFields`.                                                                                             |
| `converters`            | Map&lt;String, ScalarConverter&gt; |                                                                   | Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.                                                                                                                        |
| `packageName`           | String                          | yes                                                               | Target package name for generated code.<br />**Command line property is**: `packageName`.                                                                                                                                                                                           |
| `queryFiles`            | FileCollection                  |                                                                   | List of query files to be processed. Instead of a list of files to be processed you can specify `queryFileDirectory` directory instead. If this property is specified it will take precedence over the corresponding directory property.                                           |
| `queryFileDirectory`    | String                          |                                                                   | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br />**Default value is:** `src/main/resources`.<br />**Command line property is**: `queryFileDirectory`. |
| `schemaFile`            | File                            | `schemaFileName` or `schemaFile` has to be provided | GraphQL schema file that will be used to generate client code.                                                                                                                                                                                                                            |
| `schemaFileName`        | String                          | `schemaFileName` or `schemaFile` has to be provided | Path to GraphQL schema file that will be used to generate client code.<br />**Command line property is**: `schemaFileName`.                                                                                                                                                         |

### graphqlGenerateTestClient

Task that generates GraphQL Kotlin test client and corresponding data classes based on the provided GraphQL queries that are
evaluated against target Graphql schema. Individual test clients with their specific data models are generated for each query
file and are placed under specified `packageName`. When this task is added to the project it will automatically configure
itself as a dependency of a `compileTestKotlin` task and resulting generated code will be automatically added to the project
test source set.

**Properties**

| Property                       | Type                            | Required                                                          | Description                                                                                                                                                                                                                                                                               |
| ------------------------------ | ------------------------------- | ----------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `allowDeprecatedFields` | Boolean                         |                                                                   | Boolean flag indicating whether selection of deprecated fields is allowed or not.<br />**Default value is:** `false`.<br />**Command line property is**: `allowDeprecatedFields`.                                                                                             |
| `converters`            | Map&lt;String, ScalarConverter&gt; |                                                                   | Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.                                                                                                                        |
| `packageName`           | String                          | yes                                                               | Target package name for generated code.<br />**Command line property is**: `packageName`.                                                                                                                                                                                           |
| `queryFiles`            | FileCollection                  |                                                                   | List of query files to be processed. Instead of a list of files to be processed you can specify `queryFileDirectory` directory instead. If this property is specified it will take precedence over the corresponding directory property.                                           |
| `queryFileDirectory`    | String                          |                                                                   | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br />**Default value is:** `src/test/resources`.<br />**Command line property is**: `queryFileDirectory`. |
| `schemaFile`            | File                            | `schemaFileName` or `schemaFile` has to be provided | GraphQL schema file that will be used to generate client code.                                                                                                                                                                                                                            |
| `schemaFileName`        | String                          | `schemaFileName` or `schemaFile` has to be provided | Path to GraphQL schema file that will be used to generate client code.<br />**Command line property is**: `schemaFileName`.                                                                                                                                                         |

### graphqlIntrospectSchema

Task that executes GraphQL introspection query against specified `endpoint` and saves the underlying schema file as
`schema.graphql` under build directory. In general, this task provides limited functionality by itself and instead
should be used to generate input for the subsequent `graphqlGenerateClient` task.

**Properties**

| Property               | Type                | Required | Description                                                                                                                                                                               |
| ---------------------- | ------------------- | -------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `endpoint`      | String              | yes      | Target GraphQL server endpoint that will be used to execute introspection queries.<br />**Command line property is**: `endpoint`.                                                   |
| `headers`       | Map&lt;String, Any&gt; |          | Optional HTTP headers to be specified on an introspection query.                                                                                                                          |
| `timeoutConfig` | TimeoutConfig       |          | Timeout configuration(in milliseconds) to download schema from SDL endpoint before we cancel the request.<br />**Default value are:** connect timeout = 5_000, read timeout = 15_000.<br /> |

## Examples

### Downloading Schema SDL

GraphQL endpoints are often public and as such many servers might disable introspection queries in production environment.
Since GraphQL schema is needed to generate type safe clients, as alternative GraphQL servers might expose private
endpoints (e.g. accessible only from within network, etc) that could be used to download schema in Schema Definition
Language (SDL) directly. `graphqlDownloadSDL` task requires target GraphQL server `endpoint` to be specified and can
be executed directly from the command line by explicitly passing `endpoint` parameter

```shell script

$ gradle graphqlDownloadSDL --endpoint="http://localhost:8080/sdl"

```

Task can also be explicitly configured in your Gradle build file

```kotlin

// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask

val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
    endpoint.set("http://localhost:8080/sdl")
}

```

&gt; NOTE: This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly
&gt; invoke it OR configure it as a dependency of some other task.

### Introspecting Schema

Introspection task requires target GraphQL server `endpoint` to be specified. Task can be executed directly from the
command line by explicitly passing endpoint parameter

```shell script

$ gradle graphqlIntrospectSchema --endpoint="http://localhost:8080/graphql"

```

Task can also be explicitly configured in your Gradle build file

```kotlin

// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {
    endpoint.set("http://localhost:8080/graphql")
}

```

&gt; NOTE: This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly
&gt; invoke it OR configure it as a dependency of some other task.

### Generating Client

GraphQL Kotlin client code is generated based on the provided queries that will be executed against target GraphQL `schemaFile`.
Separate class is generated for each provided GraphQL query and are saved under specified `packageName`. When using default
configuration and storing GraphQL queries under `src/main/resources` directories, task can be executed directly from the
command line by explicitly providing required properties.

```shell script

$ gradle graphqlGenerateClient --schemaFileName"mySchema.graphql" --packageName="com.example.generated"

```

Task can also be explicitly configured in your Gradle build file

```kotlin

// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
  packageName.set("com.example.generated")
  schemaFileName.set("mySchema.graphql")
}

```

This will process all GraphQL queries located under `src/main/resources` and generate corresponding GraphQL Kotlin clients.
Generated classes will be automatically added to the project compile sources.

### Generating Client with Custom Scalars

By default, all custom GraphQL scalars will be serialized as Strings. You can override this default behavior by specifying
custom [scalar converter](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-client/src/main/kotlin/com/expediagroup/graphql/client/converter/ScalarConverter.kt).

For example given following custom scalar in our GraphQL schema

```graphql

scalar UUID

```

We can create a custom converter to automatically convert this custom scalar to `java.util.UUID`

```kotlin

package com.example

import com.expediagroup.graphql.client.converter.ScalarConverter
import java.util.UUID

class UUIDScalarConverter : ScalarConverter<UUID> {
    override fun toScalar(rawValue: String): UUID = UUID.fromString(rawValue)
    override fun toJson(value: UUID): String = value.toString()
}

```

Afterwards we need to configure our plugin to use this custom converter

```kotlin

// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
  packageName.set("com.example.generated")
  schemaFileName.set("mySchema.graphql")
  converters.put("UUID", ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter"))
}

```

### Generating Test Client

GraphQL Kotlin test client code is generated based on the provided queries that will be executed against target GraphQL `schemaFile`.
Separate class is generated for each provided GraphQL query and are saved under specified `packageName`. When using default
configuration and storing GraphQL queries under `src/test/resources` directories, task can be executed directly from the
command line by explicitly providing required properties.

```shell script

$ gradle graphqlGenerateTestClient --schemaFileName"mySchema.graphql" --packageName="com.example.generated"

```

Task can also be explicitly configured in your Gradle build file

```kotlin

// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlGenerateTestClient by tasks.getting(GraphQLGenerateClientTask::class) {
  packageName.set("com.example.generated")
  schemaFileName.set("mySchema.graphql")
}

```

This will process all GraphQL queries located under `src/test/resources` and generate corresponding GraphQL Kotlin clients.
Generated classes will be automatically added to the project test compile sources.

&gt; NOTE: `graphqlGenerateTestClient` cannot be configured through the `graphql` extension and has to be explicitly configured.

### Complete Minimal Configuration Example

Following is the minimal configuration that runs introspection query against a target GraphQL server and generates a
corresponding schema. This generated schema is subsequently used to generate GraphQL client code based on the queries
provided under `src/main/resources` directory.

```kotlin

// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.graphql

graphql {
  client {
    endpoint = "http://localhost:8080/graphql"
    packageName = "com.example.generated"
  }
}

```

Above configuration is equivalent to the following

```kotlin

// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask

val graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {
  endpoint.set("http://localhost:8080/graphql")
}
val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
  packageName.set("com.example.generated")
  schemaFile.set(graphqlIntrospectSchema.outputFile)
  dependsOn("graphqlIntrospectSchema")
}

```

### Complete Configuration Example

Following is a configuration example that downloads schema SDL from a target GraphQL server that is then used to generate
the GraphQL client code based on the provided query.

```kotlin

// build.gradle.kts
import com.expediagroup.graphql.plugin.config.TimeoutConfig
import com.expediagroup.graphql.plugin.generator.ScalarConverterMapping
import com.expediagroup.graphql.plugin.gradle.graphql

graphql {
  client {
    sdlEndpoint = "http://localhost:8080/sdl"
    packageName = "com.example.generated"
    // optional configuration
    allowDeprecatedFields = true
    headers["X-Custom-Header"] = "My-Custom-Header"
    converters["UUID"] = ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter")
    queryFiles.add(file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql"))
    timeout {
        connect = 10_000
        read = 30_000
    }
  }
}

```

Above configuration is equivalent to the following

```kotlin

// build.gradle.kts
import com.expediagroup.graphql.plugin.config.TimeoutConfig
import com.expediagroup.graphql.plugin.generator.ScalarConverterMapping
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask

val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
    endpoint.set("http://localhost:8080/sdl")
    headers.put("X-Custom-Header", "My-Custom-Header")
    timeoutConfig.set(TimeoutConfig(connect = 10_000, read = 30_000))
}
val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
    packageName.set("com.example.generated")
    schemaFile.set(graphqlDownloadSDL.outputFile)
    // optional
    allowDeprecatedFields.set(true)
    converters.put("UUID", ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter"))
    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")

    dependsOn("graphqlDownloadSDL")
}

```
