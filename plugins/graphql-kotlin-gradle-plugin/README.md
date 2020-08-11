# GraphQL Kotlin Gradle Plugin
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-gradle-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-gradle-plugin%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-gradle-plugin.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-gradle-plugin)
[![Plugin Portal](https://img.shields.io/maven-metadata/v?label=Plugin%20Portal&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fcom%2Fexpediagroup%2Fgraphql-kotlin-gradle-plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/com.expediagroup.graphql)

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
[GraphQLPluginExtension](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/plugins/graphql-kotlin-gradle-plugin/src/main/kotlin/com/expediagroup/graphql/plugin/gradle/GraphQLPluginExtension.kt).
This extension can be used to configure global options instead of explicitly configuring individual tasks.

```kotlin
import com.expediagroup.graphql.plugin.generator.GraphQLClientType
import com.expediagroup.graphql.plugin.gradle.graphql

graphql {
  client {
    // Boolean flag indicating whether or not selection of deprecated fields is allowed.
    allowDeprecatedFields = false
    // Type of GraphQL client implementation to generate.
    clientType = GraphQLClientType.DEFAULT
    // Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.
    converters["UUID"] = ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter")
    // GraphQL server endpoint that will be used to for running introspection queries. Alternatively you can download schema directly from `sdlEndpoint`.
    endpoint = "http://localhost:8080/graphql"
    // Optional HTTP headers to be specified on an introspection query or SDL request.
    headers["X-Custom-Header"] = "Custom-Header-Value"
    // Target package name to be used for generated classes.
    packageName = "com.example.generated"
    // Optional list of query files to be processed, if not specified will default to all query files under src/main/resources.
    queryFiles.add(file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql"))
    // GraphQL server SDL endpoint that will be used to download schema. Alternatively you can run introspection query against `endpoint`.
    sdlEndpoint = "http://localhost:8080/sdl"
    // Timeout configuration for introspection query/downloading SDL
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
`graphql`. Additional information on the available tasks and their available configuration options can be found on our
[documentation pages](https://expediagroup.github.io/graphql-kotlin/docs/plugins/gradle-plugin).

### graphqlDownloadSDL

Task that attempts to download GraphQL schema in SDL format from the specified `endpoint` and saves the underlying
schema file as `schema.graphql` under build directory. In general, this task provides limited functionality by itself
and could be used as an alternative to `graphqlIntrospectSchema` to generate input for the subsequent
`graphqlGenerateClient` task.

**Properties**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `endpoint` | String | yes | Target GraphQL server SDL endpoint that will be used to download schema.<br/>**Command line property is**: `endpoint`. |
| `headers` | Map<String, Any> | | Optional HTTP headers to be specified on a SDL request. |
| `timeoutConfig` | TimeoutConfig | | Optional timeout configuration(in milliseconds) to download schema from SDL endpoint before we cancel the request.<br/>**Default value are:** connect timeout = 5_000, read timeout = 15_000.<br/>|

### graphqlGenerateClient

Task that generates GraphQL Kotlin client and corresponding data classes based on the provided GraphQL queries that are
evaluated against target Graphql schema. Individual clients with their specific data models are generated for each query
file and are placed under specified `packageName`. When this task is added to the project, either through explicit configuration
or through the `graphql` extension, it will automatically configure itself as a dependency of a `compileKotlin` task and
resulting generated code will be automatically added to the project main source set.

**Properties**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `allowDeprecatedFields` | Boolean | | Boolean flag indicating whether selection of deprecated fields is allowed or not.<br/>**Default value is:** `false`.<br/>**Command line property is**: `allowDeprecatedFields`. |
| `clientType` | GraphQLClientType | | Enum value that specifies target GraphQL client type implementation.<br/>**Default value is:** `GraphQLClientType.DEFAULT`. |
| `converters` | Map<String, ScalarConverter> | | Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values. |
| `packageName` | String | yes | Target package name for generated code.<br/>**Command line property is**: `packageName`. |
| `queryFiles` | FileCollection | | List of query files to be processed. Instead of a list of files to be processed you can specify `queryFileDirectory` directory instead. If this property is specified it will take precedence over the corresponding directory property. |
| `queryFileDirectory` | String | | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br/>**Default value is:** `src/main/resources`.<br/>**Command line property is**: `queryFileDirectory`. |
| `schemaFile` | File | `schemaFileName` or `schemaFile` has to be provided | GraphQL schema file that will be used to generate client code. |
| `schemaFileName` | String | `schemaFileName` or `schemaFile` has to be provided | Path to GraphQL schema file that will be used to generate client code.<br/>**Command line property is**: `schemaFileName`. |

### graphqlGenerateTestClient

Task that generates GraphQL Kotlin test client and corresponding data classes based on the provided GraphQL queries that are
evaluated against target Graphql schema. Individual test clients with their specific data models are generated for each query
file and are placed under specified `packageName`. When this task is added to the project it will automatically configure
itself as a dependency of a `compileTestKotlin` task and resulting generated code will be automatically added to the project
test source set.

**Properties**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `allowDeprecatedFields` | Boolean | | Boolean flag indicating whether selection of deprecated fields is allowed or not.<br/>**Default value is:** `false`.<br/>**Command line property is**: `allowDeprecatedFields`. |
| `clientType` | GraphQLClientType | | Enum value that specifies target GraphQL client type implementation.<br/>**Default value is:** `GraphQLClientType.DEFAULT`. |
| `converters` | Map<String, ScalarConverter> | | Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values. |
| `packageName` | String | yes | Target package name for generated code.<br/>**Command line property is**: `packageName`. |
| `queryFiles` | FileCollection | | List of query files to be processed. Instead of a list of files to be processed you can specify `queryFileDirectory` directory instead. If this property is specified it will take precedence over the corresponding directory property. |
| `queryFileDirectory` | String | | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br/>**Default value is:** `src/test/resources`.<br/>**Command line property is**: `queryFileDirectory`. |
| `schemaFile` | File | `schemaFileName` or `schemaFile` has to be provided | GraphQL schema file that will be used to generate client code. |
| `schemaFileName` | String | `schemaFileName` or `schemaFile` has to be provided | Path to GraphQL schema file that will be used to generate client code.<br/>**Command line property is**: `schemaFileName`. |

### graphqlIntrospectSchema

Task that executes GraphQL introspection query against specified `endpoint` and saves the underlying schema file as
`schema.graphql` under build directory. In general, this task provides limited functionality by itself and instead
should be used to generate input for the subsequent `graphqlGenerateClient` task.

**Properties**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `endpoint` | String | yes | Target GraphQL server endpoint that will be used to execute introspection queries.<br/>**Command line property is**: `endpoint`. |
| `headers` | Map<String, Any> | | Optional HTTP headers to be specified on an introspection query. |
| `timeoutConfig` | TimeoutConfig | | Optional timeout configuration(in milliseconds) to execute introspection query before we cancel the request.<br/>**Default value are:** connect timeout = 5_000, read timeout = 15_000.<br/>|

## Documentation

Additional information can be found in our [documentation](https://expediagroup.github.io/graphql-kotlin/docs/plugins/gradle-plugin)
and the [Javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-gradle-plugin) of all published versions.

If you have a question about something you can not find in our documentation or Javadocs, feel free to
[create an issue](https://github.com/ExpediaGroup/graphql-kotlin/issues) and tag it with the question label.
