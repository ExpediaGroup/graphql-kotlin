---
id: version-4.0.0-alpha.13-gradle-plugin-usage
title: Gradle Plugin Usage
sidebar_label: Usage
original_id: gradle-plugin-usage
---

## Downloading Schema SDL

GraphQL endpoints are often public and as such many servers might disable introspection queries in production environment.
Since GraphQL schema is needed to generate type safe clients, as alternative GraphQL servers might expose private
endpoints (e.g. accessible only from within network, etc) that could be used to download schema in Schema Definition
Language (SDL) directly. `graphqlDownloadSDL` task requires target GraphQL server `endpoint` to be specified and can
be executed directly from the command line by explicitly passing `endpoint` parameter

```shell script
$ gradle graphqlDownloadSDL --endpoint="http://localhost:8080/sdl"
```

Task can also be explicitly configured in your Gradle build file

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask

val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
    endpoint.set("http://localhost:8080/sdl")
}
```

<!--Groovy-->

```groovy
//build.gradle
graphqlDownloadSDL {
    endpoint = "http://localhost:8080/sdl"
}
```

<!--END_DOCUSAURUS_CODE_TABS-->

>NOTE: This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly
>invoke it OR configure it as a dependency of some other task.

## Introspecting Schema

Introspection task requires target GraphQL server `endpoint` to be specified. Task can be executed directly from the
command line by explicitly passing endpoint parameter

```shell script
$ gradle graphqlIntrospectSchema --endpoint="http://localhost:8080/graphql"
```

Task can also be explicitly configured in your Gradle build file

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {
    endpoint.set("http://localhost:8080/graphql")
}
```

<!--Groovy-->

```groovy
//build.gradle
graphqlIntrospectSchema {
    endpoint = "http://localhost:8080/graphql"
}
```

<!--END_DOCUSAURUS_CODE_TABS-->

>NOTE: This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly
>invoke it OR configure it as a dependency of some other task.

## Generating Generic Client

GraphQL Kotlin client code is generated based on the provided queries that will be executed against target GraphQL `schemaFile`.
Separate class is generated for each provided GraphQL query and are saved under specified `packageName`. When using default
configuration and storing GraphQL queries under `src/main/resources` directories, task can be executed directly from the
command line by explicitly providing required properties.

```shell script
$ gradle graphqlGenerateClient --schemaFileName"mySchema.graphql" --packageName="com.example.generated"
```

Task can also be explicitly configured in your Gradle build file

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
  packageName.set("com.example.generated")
  schemaFileName.set("mySchema.graphql")
}
```

<!--Groovy-->

```groovy
//build.gradle
graphqlGenerateClient {
    packageName = "com.example.generated"
    schemaFileName = "mySchema.graphql"
}
```

<!--END_DOCUSAURUS_CODE_TABS-->

This will process all GraphQL queries located under `src/main/resources` and generate corresponding GraphQL Kotlin clients.
Generated classes will be automatically added to the project compile sources.

## Generating Ktor or WebClient Based Client

By default, GraphQL Kotlin plugins will generate client code that uses generic `GraphQLClient` interface. Additional
configuration options are available if you generate type specific client code but it will also put a restriction on
type of client that can be used for your queries.

For example in order to generate Ktor based HTTP client we need to specify `GraphQLClientType.KTOR` client type. Alternatively,
if you would like to use WebClient implementation instead you need to specify `GraphQLClientType.WEBCLIENT` instead.

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.config.GraphQLClientType
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
  clientType.set(GraphQLClientType.KTOR)
  packageName.set("com.example.generated")
  schemaFileName.set("mySchema.graphql")
}
```

<!--Groovy-->

```groovy
//build.gradle
import com.expediagroup.graphql.plugin.gradle.config.GraphQLClientType

graphqlGenerateClient {
    clientType = GraphQLClientType.KTOR
    packageName = "com.example.generated"
    schemaFileName = "mySchema.graphql"
}
```

<!--END_DOCUSAURUS_CODE_TABS-->

## Generating Client with Custom Scalars

By default, all custom GraphQL scalars will be serialized as Strings. You can override this default behavior by specifying
custom [scalar converter](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-client/src/main/kotlin/com/expediagroup/graphql/client/converter/ScalarConverter.kt).

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

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
  packageName.set("com.example.generated")
  schemaFileName.set("mySchema.graphql")
  customScalars.add(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))
}
```

<!--Groovy-->

```groovy
//build.gradle
import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar

graphqlGenerateClient {
    packageName = "com.example.generated"
    schemaFileName = "mySchema.graphql"
    customScalars.add(new GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))
}
```

<!--END_DOCUSAURUS_CODE_TABS-->

## Generating Test Client

GraphQL Kotlin test client code is generated based on the provided queries that will be executed against target GraphQL `schemaFile`.
Separate class is generated for each provided GraphQL query and are saved under specified `packageName`. When using default
configuration and storing GraphQL queries under `src/test/resources` directories, task can be executed directly from the
command line by explicitly providing required properties.

```shell script
$ gradle graphqlGenerateTestClient --schemaFileName"mySchema.graphql" --packageName="com.example.generated"
```

Task can also be explicitly configured in your Gradle build file

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlGenerateTestClient by tasks.getting(GraphQLGenerateClientTask::class) {
  packageName.set("com.example.generated")
  schemaFileName.set("mySchema.graphql")
}
```

<!--Groovy-->

```groovy
//build.gradle
graphqlGenerateTestClient {
    packageName = "com.example.generated"
    schemaFileName = "mySchema.graphql"
}
```

<!--END_DOCUSAURUS_CODE_TABS-->

This will process all GraphQL queries located under `src/test/resources` and generate corresponding GraphQL Kotlin clients.
Generated classes will be automatically added to the project test compile sources.

>NOTE: `graphqlGenerateTestClient` cannot be configured through the `graphql` extension and has to be explicitly configured.

## Minimal Client Configuration Example

Following is the minimal configuration that runs introspection query against a target GraphQL server and generates a
corresponding schema. This generated schema is subsequently used to generate GraphQL client code based on the queries
provided under `src/main/resources` directory.

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

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

<!--Groovy-->

```groovy
graphql {
    client {
        endpoint = "http://localhost:8080/graphql"
        packageName = "com.example.generated"
    }
}
```

Above configuration is equivalent to the following

```groovy
// build.gradle
graphqlIntrospectSchema {
    endpoint = "http://localhost:8080/graphql"
}
graphqlGenerateClient {
    packageName = "com.example.generated"
    schemaFile = graphqlIntrospectSchema.outputFile
    dependsOn graphqlIntrospectSchema
}
```

<!--END_DOCUSAURUS_CODE_TABS-->

## Complete Client Configuration Example

Following is a configuration example that downloads schema SDL from a target GraphQL server that is then used to generate
the GraphQL Ktor client code based on the provided query.

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.config.GraphQLClientType
import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.graphql

graphql {
  client {
    sdlEndpoint = "http://localhost:8080/sdl"
    packageName = "com.example.generated"
    // optional configuration
    allowDeprecatedFields = true
    clientType = GraphQLClientType.KTOR
    customScalars = listOf(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))
    headers = mapOf("X-Custom-Header" to "My-Custom-Header")
    queryFiles = listOf(file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql"))
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
import com.expediagroup.graphql.plugin.gradle.config.GraphQLClientType
import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
    endpoint.set("http://localhost:8080/sdl")
    headers.put("X-Custom-Header", "My-Custom-Header")
    timeoutConfig.set(TimeoutConfiguration(connect = 10_000, read = 30_000))
}
val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
    packageName.set("com.example.generated")
    schemaFile.set(graphqlDownloadSDL.outputFile)
    // optional
    allowDeprecatedFields.set(true)
    clientType.set(GraphQLClientType.KTOR)
    customScalars.add(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))
    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")

    dependsOn("graphqlDownloadSDL")
}
```

<!--Groovy-->

```groovy
// build.gradle
import com.expediagroup.graphql.plugin.gradle.config.GraphQLClientType
import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar

graphql {
    client {
        sdlEndpoint = "http://localhost:8080/sdl"
        packageName = "com.example.generated"
        // optional configuration
        allowDeprecatedFields = true
        clientType = GraphQLClientType.KTOR
        customScalars = [new GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter")]
        headers = ["X-Custom-Header" : "My-Custom-Header"]
        queryFiles = [file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")]
        timeout { t ->
            t.connect = 10000
            t.read = 30000
        }
    }
}
```

Above configuration is equivalent to the following

```groovy
//build.gradle
import com.expediagroup.graphql.plugin.gradle.config.GraphQLClientType
import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration

graphqlDownloadSDL {
    endpoint = "http://localhost:8080/sdl"
    headers["X-Custom-Header"] = "My-Custom-Header"
    timeoutConfig = new TimeoutConfiguration(10000, 30000)
}
graphqlGenerateClient {
    packageName = "com.example.generated"
    schemaFile = graphqlDownloadSDL.outputFile
    // optional
    allowDeprecatedFields = true
    clientType = GraphQLClientType.KTOR
    customScalars.add(new GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))
    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")

    dependsOn graphqlDownloadSDL
}
```

<!--END_DOCUSAURUS_CODE_TABS-->

## Generating Multiple Clients

GraphQL Kotlin Gradle Plugin registers tasks for generation of a client queries targeting single GraphQL endpoint. You
can generate queries targeting additional endpoints by explicitly creating and configuring additional tasks.

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
    endpoint.set("http://localhost:8080/sdl")
}
val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
    packageName.set("com.example.generated.first")
    schemaFile.set(graphqlDownloadSDL.outputFile)
    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyFirstQuery.graphql")
    dependsOn("graphqlDownloadSDL")
}

val graphqlDownloadOtherSDL by tasks.creating(GraphQLDownloadSDLTask::class) {
    endpoint.set("http://localhost:8081/sdl")
}
val graphqlGenerateOtherClient by tasks.creating(GraphQLGenerateClientTask::class) {
    packageName.set("com.example.generated.second")
    schemaFile.set(graphqlDownloadOtherSDL.outputFile)
    queryFiles.from("${project.projectDir}/src/main/resources/queries/MySecondQuery.graphql")
    dependsOn("graphqlDownloadOtherSDL")
}
```

<!--Groovy-->

```groovy
//build.gradle
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

graphqlDownloadSDL {
    endpoint = "http://localhost:8080/sdl"
}
graphqlGenerateClient {
    packageName = "com.example.generated.first"
    schemaFile = graphqlDownloadSDL.outputFile
    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyFirstQuery.graphql")

    dependsOn graphqlDownloadSDL
}

task graphqlDownloadOtherSDL(type: GraphQLDownloadSDLTask) {
    endpoint = "http://localhost:8081/sdl"
}
task graphqlGenerateOtherClient(type: GraphQLGenerateClientTask) {
    packageName = "com.other.generated.second"
    schemaFile = graphqlDownloadOtherSDL.outputFile
    queryFiles.from("${project.projectDir}/src/main/resources/queries/MySecondQuery.graphql")

    dependsOn graphqlDownloadOtherSDL
}
```

<!--END_DOCUSAURUS_CODE_TABS-->

## Generating SDL Example

GraphQL schema can be generated directly from your source code using reflections. `graphqlGenerateSDL` will scan your
classpath looking for classes implementing `Query`, `Mutation` and `Subscription` marker interfaces and then generates the
corresponding GraphQL schema using `graphql-kotlin-schema-generator` and default `NoopSchemaGeneratorHooks`. In order to
limit the amount of packages to scan, this task requires users to provide a list of `packages` that can contain GraphQL
types.

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.graphql

graphql {
  schema {
    packages = listOf("com.example")
  }
}
```

Above configuration is equivalent to the following task definition

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.example"))
}
```

<!--Groovy-->

```groovy
// build.gradle
graphql {
  schema {
    packages = ["com.example"]
  }
}
```

Above configuration is equivalent to the following task definition

```groovy
//build.gradle
graphqlGenerateSDL {
    packages = ["com.example"]
}
```

<!--END_DOCUSAURUS_CODE_TABS-->

>NOTE: This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly
>invoke it OR configure it as a dependency of some other task.

## Generating SDL with Custom Hooks Provider Example

Plugin will default to use `NoopSchemaGeneratorHooks` to generate target GraphQL schema. If your project uses custom hooks
or needs to generate the federated GraphQL schema, you will need to provide an instance of `SchemaGeneratorHooksProvider`
service provider that will be used to create an instance of your custom hooks.

`graphqlGenerateSDL` utilizes [ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html)
mechanism to dynamically load available `SchemaGeneratorHooksProvider` service providers from the classpath. Service provider
can be provided as part of your project, included in one of your project dependencies or through explicitly provided artifact.
See [Schema Generator Hooks Provider](./hooks-provider.md) for additional details on how to create custom hooks service provider.
Configuration below shows how to configure GraphQL Kotlin plugin with explicitly provided artifact to generate federated
GraphQL schema.

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.graphql

graphql {
  schema {
    packages = listOf("com.example")
  }
}

dependencies {
    graphqlSDL("com.expediagroup:graphql-kotlin-federated-hooks-provider:$graphQLKotlinVersion")
}
```

Above configuration is equivalent to the following task definition

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.example"))
}

dependencies {
    graphqlSDL("com.expediagroup:graphql-kotlin-federated-hooks-provider:$graphQLKotlinVersion")
}
```

<!--Groovy-->

```groovy
// build.gradle
graphql {
  schema {
    packages = ["com.example"]
  }
}

dependencies {
    graphqlSDL "com.expediagroup:graphql-kotlin-federated-hooks-provider:$DEFAULT_PLUGIN_VERSION"
}
```

Above configuration is equivalent to the following task definition

```groovy
//build.gradle
graphqlGenerateSDL {
    packages = ["com.example"]
}

dependencies {
    graphqlSDL "com.expediagroup:graphql-kotlin-federated-hooks-provider:$DEFAULT_PLUGIN_VERSION"
}
```

<!--END_DOCUSAURUS_CODE_TABS-->

>NOTE: This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly
>invoke it OR configure it as a dependency of some other task.
