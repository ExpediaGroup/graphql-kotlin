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
import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.expediagroup.graphql.plugin.gradle.graphql

graphql {
  client {
      // Boolean flag indicating whether or not selection of deprecated fields is allowed.
      allowDeprecatedFields = false
      // List of custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.
      customScalars = listOf(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))
      // GraphQL server endpoint that will be used to for running introspection queries. Alternatively you can download schema directly from `sdlEndpoint` or specify local `schemaFile`.
      endpoint = "http://localhost:8080/graphql"
      // Optional HTTP headers to be specified on an introspection query or SDL request.
      headers = mapOf("X-Custom-Header" to "Custom-Header-Value")
      // Target package name to be used for generated classes.
      packageName = "com.example.generated"
      // Custom directory containing query files, defaults to src/main/resources
      queryFileDirectory = "${project.projectDir}/src/main/resources/queries"
      // Optional list of query files to be processed, takes precedence over queryFileDirectory
      queryFiles = listOf(file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql"))
      // GraphQL schema file. Can be used instead of `endpoint` or `sdlEndpoint`.
      schemaFile = file("${project.projectDir}/src/main/resources/myLocalSchema.graphql")
      // GraphQL server SDL endpoint that will be used to download schema. Alternatively you can run introspection query against `endpoint` or specify local `schemaFile`.
      sdlEndpoint = "http://localhost:8080/sdl"
      // JSON serializer that will be used to generate the data classes.
      serializer = GraphQLSerializer.JACKSON
      // Timeout configuration for introspection query/downloading SDL
      timeout {
          // Connect timeout in milliseconds
          connect = 5_000
          // Read timeout in milliseconds
          read = 15_000
      }
      // Opt-in flag to wrap nullable arguments in OptionalInput that distinguish between null and undefined value.
      useOptionalInputWrapper = false
  }
  schema {
      // List of supported packages that can contain GraphQL schema type definitions
      packages = listOf("com.example")
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
| `outputFile` | File | | Target GraphQL schema file to be generated.<br/>**Default value is:** `${project.buildDir}/schema.graphql` |
| `timeoutConfig` | TimeoutConfig | | Optional timeout configuration(in milliseconds) to download schema from SDL endpoint before we cancel the request.<br/>**Default value are:**<br/>connect timeout = 5_000<br/>read timeout = 15_000.<br/>|

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
| `customScalars` | List<GraphQLScalar> | | List of custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values. |
| `packageName` | String | yes | Target package name for generated code.<br/>**Command line property is**: `packageName`. |
| `queryFiles` | FileCollection | | List of query files to be processed. Instead of a list of files to be processed you can specify `queryFileDirectory` directory instead. If this property is specified it will take precedence over the corresponding directory property. |
| `queryFileDirectory` | Directory | | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br/>**Default value is:** `src/main/resources`. |
| `serializer` | GraphQLSerializer | | JSON serializer that will be used to generate the data classes.<br/>**Default value is:** `GraphQLSerializer.JACKSON`. |
| `schemaFile` | File | yes | GraphQL schema file that will be used to generate client code. |
| `useOptionalInputWrapper` | Boolean | | Boolean opt-in flag to wrap nullable arguments in `OptionalInput` that distinguish between `null` and undefined/omitted value. <br/>**Default value is:** `false`.<br/>**Command line property is**: `useOptionalInputWrapper` |

### graphqlGenerateSDL

Task that generates GraphQL schema in SDL format from your source code using reflections. Utilizes `graphql-kotlin-schema-generator`
to generate the schema from classes implementing `graphql-kotlin-types` marker `Query`, `Mutation` and `Subscription` interfaces.
In order to limit the amount of packages to scan, this task requires users to provide a list of `packages` that can contain
GraphQL types.

**Properties**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `packages` | List<String> | yes | List of supported packages that can be scanned to generate SDL. |
| `schemaFile` | File | | Target GraphQL schema file to be generated.<br/>**Default value is:** `${project.buildDir}/schema.graphql` |

By default, this task will attempt to generate the schema using `NoopSchemaGeneratorHooks`. If you need to customize your
schema generation process, you will need to provide your custom instance of `SchemaGeneratorHooksProvider` service provider.
Service provider can be provided as part of your project, included in one of your project dependencies or through explicitly
provided artifact to the `graphqlSDL` configuration.

```kotlin
// build.gradle.kts
dependencies {
    graphqlSDL("com.expediagroup:graphql-kotlin-federated-hooks-provider:$graphQLKotlinVersion")
}
```

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
| `customScalars` | List<GraphQLScalar> | | List of custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values. |
| `packageName` | String | yes | Target package name for generated code.<br/>**Command line property is**: `packageName`. |
| `queryFiles` | FileCollection | | List of query files to be processed. Instead of a list of files to be processed you can specify `queryFileDirectory` directory instead. If this property is specified it will take precedence over the corresponding directory property. |
| `queryFileDirectory` | Directory | | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br/>**Default value is:** `src/test/resources`. |
| `serializer` | GraphQLSerializer | | JSON serializer that will be used to generate the data classes.<br/>**Default value is:** `GraphQLSerializer.JACKSON`. |
| `schemaFile` | File | yes | GraphQL schema file that will be used to generate client code. |
| `useOptionalInputWrapper` | Boolean | | Boolean opt-in flag to wrap nullable arguments in `OptionalInput` that distinguish between `null` and undefined/omitted value. <br/>**Default value is:** `false`.<br/>**Command line property is**: `useOptionalInputWrapper` |

### graphqlIntrospectSchema

Task that executes GraphQL introspection query against specified `endpoint` and saves the underlying schema file as
`schema.graphql` under build directory. In general, this task provides limited functionality by itself and instead
should be used to generate input for the subsequent `graphqlGenerateClient` task.

**Properties**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `endpoint` | String | yes | Target GraphQL server endpoint that will be used to execute introspection queries.<br/>**Command line property is**: `endpoint`. |
| `headers` | Map<String, Any> | | Optional HTTP headers to be specified on an introspection query. |
| `outputFile` | File | | Target GraphQL schema file to be generated.<br/>**Default value is:** `${project.buildDir}/schema.graphql` |
| `timeoutConfig` | TimeoutConfig | | Optional timeout configuration(in milliseconds) to execute introspection query before we cancel the request.<br/>**Default value are:**<br/>connect timeout = 5_000</br>>read timeout = 15_000.<br/>|

## Testing

This project uses [Gradle TestKit](https://docs.gradle.org/current/userguide/test_kit.html) to run functional integration
tests using `GradleRunner`. Integration tests apply plugin and verifies its correctness by running embedded Gradle against
auto-generated projects. Since tests are run in separate JVMs, there is no JaCoCo coverage calculated for those tests.

## Documentation

Additional information can be found in our [documentation](https://expediagroup.github.io/graphql-kotlin/docs/plugins/gradle-plugin)
and the [Javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-gradle-plugin) of all published versions.

If you have a question about something you can not find in our documentation or Javadocs, feel free to
[create an issue](https://github.com/ExpediaGroup/graphql-kotlin/issues) and tag it with the question label.
