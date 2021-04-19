---
id: maven-plugin-goals
title: Maven Plugin Goals
sidebar_label: Goals
---

GraphQL Kotlin Maven Plugin provides functionality to generate a lightweight GraphQL HTTP client and generate GraphQL
schema directly from your source code.

:::info
This plugin is dependent on Kotlin compiler plugin as it generates Kotlin source code that needs to be compiled.
:::

## Goals

You can find detailed information about `graphql-kotlin-maven-plugin` and all its goals by running `mvn help:describe -Dplugin=com.expediagroup:graphql-kotlin-maven-plugin -Ddetail`.

### download-sdl

GraphQL endpoints are often public and as such many servers might disable introspection queries in production environment.
Since GraphQL schema is needed to generate type safe clients, as alternative GraphQL servers might expose private
endpoints (e.g. accessible only from within network, etc) that could be used to download schema in Schema Definition
Language (SDL) directly. This Mojo attempts to download schema from the specified `graphql.endpoint`, validates the
result whether it is a valid schema and saves it locally in a specified target file (defaults to `schema.graphql` under
build directory). In general, this goal provides limited functionality by itself and instead should be used to generate
input for the subsequent `generate-client` goal.

**Attributes**

* *Default Lifecycle Phase*: `generate-sources`

**Parameters**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `endpoint` | String | yes | Target GraphQL server SDL endpoint that will be used to download schema.<br/>**User property is**: `graphql.endpoint`. |
| `headers` | Map<String, Any> | | Optional HTTP headers to be specified on a SDL request.
| `timeoutConfiguration` | TimeoutConfiguration | | Optional timeout configuration (in milliseconds) to download schema from SDL endpoint before we cancel the request.<br/>**Default values are:**<br/>connect timeout = 5000<br/>read timeout = 15000.<br/> |
| `schemaFile` | File | | Target schema file.<br/>**Default value is**: `${project.build.directory}/schema.graphql`<br/>**User property is**: `graphql.schemaFile`. |

**Parameter Details**

* *timeoutConfiguration* - Timeout configuration that allows you to specify connect and read timeout values in milliseconds.

```xml
<timeoutConfiguration>
  <!-- timeout values in milliseconds -->
  <connect>1000</connect>
  <read>30000</read>
</timeoutConfiguration>
```

### generate-client

Generate GraphQL client code based on the provided GraphQL schema and target queries.

**Attributes**

* *Default Lifecycle Phase*: `generate-sources`
* *Requires Maven Project*
* Generated classes are automatically added to the list of compiled sources.

**Parameters**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `allowDeprecatedFields` | Boolean | | Boolean flag indicating whether selection of deprecated fields is allowed or not.<br/>**Default value is:** `false`.<br/>**User property is**: `graphql.allowDeprecatedFields`. |
| `customScalars` | `List<CustomScalar>` | | List of custom GraphQL scalar to converter mappings containing information about corresponding Java type and converter that should be used to serialize/deserialize values. |
| `outputDirectory` | File | | Target directory where to store generated files.<br/>**Default value is**: `${project.build.directory}/generated-sources/graphql` |
| `packageName` | String | yes | Target package name for generated code.<br/>**User property is**: `graphql.packageName`. |
| `queryFileDirectory` | File | | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br/>**Default value is:** `src/main/resources`. |
| `queryFiles` | `List<File>` | | List of query files to be processed. Instead of a list of files to be processed you can also specify `queryFileDirectory` directory containing all the files. If this property is specified it will take precedence over the corresponding directory property. |
| `serializer` | GraphQLSerializer | | JSON serializer that will be used to generate the data classes.<br/>**Default value is:** `GraphQLSerializer.JACKSON`. |
| `schemaFile` | String | | GraphQL schema file that will be used to generate client code.<br/>**Default value is**: `${project.build.directory}/schema.graphql`<br/>**User property is**: `graphql.schemaFile`. |

**Parameter Details**

  * *customScalars* - List of custom GraphQL scalars. Objects contain target GraphQL scalar name, corresponding Java type
  and converter that should be used to serialize/deserialize values.

  ```xml
  <customScalars>
      <customScalar>
          <!-- custom scalar UUID type -->
          <scalar>UUID</scalar>
          <!-- fully qualified Java class name of a custom scalar type -->
          <type>java.util.UUID</type>
          <!-- fully qualified Java class name of a custom com.expediagroup.graphql.client.converter.ScalarConverter
            used to convert to/from raw JSON and scalar type -->
          <converter>com.example.UUIDScalarConverter</converter>
      </customScalar>
  </customScalars>
  ```

### generate-sdl

Generates GraphQL schema in SDL format from your source code using reflections. Utilizes `graphql-kotlin-schema-generator`
to generate the schema from classes implementing `graphql-kotlin-server` marker `Query`, `Mutation` and `Subscription` interfaces.
In order to limit the amount of packages to scan, this mojo requires users to provide a list of `packages` that can contain
GraphQL types.

This MOJO utilizes [ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html)
mechanism to dynamically load available `SchemaGeneratorHooksProvider` service providers from the classpath. Service provider
can be provided as part of your project, included in one of your project dependencies or through explicitly provided artifact.
See [Schema Generator Hooks Provider](./hooks-provider.mdx) for additional details on how to create custom hooks service
provider. Configuration below shows how to configure GraphQL Kotlin plugin with explicitly provided artifact.

**Attributes**

* *Default Lifecycle Phase*: `process-classes`
* *Requires Maven Project*

**Parameters**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `packages` | `List<String>` | yes | List of supported packages that can be scanned to generate SDL. |
| `schemaFile` | File | | Target GraphQL schema file to be generated.<br/>**Default value is:** `${project.buildDir}/schema.graphql` |

### generate-test-client

Generate GraphQL test client code based on the provided GraphQL schema and target queries.

**Attributes**

* *Default Lifecycle Phase*: `generate-test-sources`
* *Requires Maven Project*
* Generated classes are automatically added to the list of test compiled sources.

**Parameters**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `allowDeprecatedFields` | Boolean | | Boolean flag indicating whether selection of deprecated fields is allowed or not.<br/>**Default value is:** `false`.<br/>**User property is**: `graphql.allowDeprecatedFields`. |
| `customScalars` | `List<CustomScalar>` | | List of custom GraphQL scalar to converter mappings containing information about corresponding Java type and converter that should be used to serialize/deserialize values. |
| `outputDirectory` | File | | Target directory where to store generated files.<br/>**Default value is**: `${project.build.directory}/generated-test-sources/graphql` |
| `packageName` | String | yes | Target package name for generated code.<br/>**User property is**: `graphql.packageName`. |
| `queryFileDirectory` | File | | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br/>**Default value is:** `src/test/resources`. |
| `queryFiles` | `List<File>` | | List of query files to be processed. Instead of a list of files to be processed you can also specify `queryFileDirectory` directory containing all the files. If this property is specified it will take precedence over the corresponding directory property. |
| `serializer` | GraphQLSerializer | | JSON serializer that will be used to generate the data classes.<br/>**Default value is:** `GraphQLSerializer.JACKSON`. |
| `schemaFile` | String | | GraphQL schema file that will be used to generate client code.<br/>**Default value is**: `${project.build.directory}/schema.graphql`<br/>**User property is**: `graphql.schemaFile`. |

**Parameter Details**

  * *customScalars* - List of custom GraphQL scalars. Objects contain target GraphQL scalar name, corresponding Java type
  and converter that should be used to serialize/deserialize values.

```xml

<customScalars>
  <customScalar>
      <!-- custom scalar UUID type -->
      <scalar>UUID</scalar>
      <!-- fully qualified Java class name of a custom scalar type -->
      <type>java.util.UUID</type>
      <!-- fully qualified Java class name of a custom com.expediagroup.graphql.client.converter.ScalarConverter
        used to convert to/from raw JSON and scalar type -->
      <converter>com.example.UUIDScalarConverter</converter>
  </customScalar>
</customScalars>

```

### introspect-schema

Executes GraphQL introspection query against specified `graphql.endpoint` and saves the result locally to a target file
(defaults to `schema.graphql` under build directory). In general, this goal provides limited functionality by itself and
instead should be used to generate input for the subsequent `generate-client` goal.

**Attributes**

* *Default Lifecycle Phase*: `generate-sources`

**Parameters**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `endpoint` | String | yes | Target GraphQL server endpoint that will be used to execute introspection queries.<br/>**User property is**: `graphql.endpoint`. |
| `headers` | Map<String, Any> | | Optional HTTP headers to be specified on an introspection query. |
| `timeoutConfiguration` | TimeoutConfiguration | | Optional timeout configuration (in milliseconds) to download schema from SDL endpoint before we cancel the request.<br/>**Default values are:**<br/>connect timeout = 5000<br/>read timeout = 15000.<br/> |
| `schemaFile` | File | | Target schema file.<br/>**Default value is**: `${project.build.directory}/schema.graphql`<br/>**User property is**: `graphql.schemaFile`. |

**Parameter Details**

* *timeoutConfiguration* - Timeout configuration that allows you to specify connect and read timeout values in milliseconds.

```xml
<timeoutConfiguration>
  <!-- timeout values in milliseconds -->
  <connect>1000</connect>
  <read>30000</read>
</timeoutConfiguration>
```
