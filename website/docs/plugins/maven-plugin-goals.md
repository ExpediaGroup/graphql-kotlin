---
id: maven-plugin-goals
title: Maven Plugin Goals
sidebar_label: Goals
---
GraphQL Kotlin Maven Plugin provides functionality to generate a lightweight GraphQL HTTP client and generate GraphQL
schema directly from your source code.

&gt; NOTE: This plugin is dependent on Kotlin compiler plugin as it generates Kotlin source code that needs to be compiled.

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

-   _Default Lifecycle Phase_: `generate-sources`

**Parameters**

| Property                      | Type                 | Required | Description                                                                                                                                                                                               |
| ----------------------------- | -------------------- | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `endpoint`             | String               | yes      | Target GraphQL server SDL endpoint that will be used to download schema.<br />**User property is**: `graphql.endpoint`.                                                                             |
| `headers`              | Map&lt;String, Any&gt;  |          | Optional HTTP headers to be specified on a SDL request.                                                                                                                                                   |
| `timeoutConfiguration` | TimeoutConfiguration |          | Optional timeout configuration (in milliseconds) to download schema from SDL endpoint before we cancel the request.<br />**Default values are:**<br />connect timeout = 5000<br />read timeout = 15000.<br /> |
| `schemaFile`           | File                 |          | Target schema file.<br />**Default value is**: `${project.build.directory}/schema.graphql`<br />**User property is**: `graphql.schemaFile`.                                                   |

**Parameter Details**

-   _timeoutConfiguration_ - Timeout configuration that allows you to specify connect and read timeout values in milliseconds.

    ```xml

    <timeoutConfiguration>
        <!-- timeout values in milliseconds 
        connect1000
        read30000
    --&gt;

    ```

### generate-client

Generate GraphQL client code based on the provided GraphQL schema and target queries.

**Attributes**

-   _Default Lifecycle Phase_: `generate-sources`
-   _Requires Maven Project_
-   Generated classes are automatically added to the list of compiled sources.

**Parameters**

| Property                       | Type               | Required | Description                                                                                                                                                                                                                                                           |
| ------------------------------ | ------------------ | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `allowDeprecatedFields` | Boolean            |          | Boolean flag indicating whether selection of deprecated fields is allowed or not.<br />**Default value is:** `false`.<br />**User property is**: `graphql.allowDeprecatedFields`.                                                                         |
| `clientType`            | GraphQLClientType  |          | Enum value that specifies target GraphQL client type implementation.<br />**Default value is:** `GraphQLClientType.DEFAULT`.                                                                                                                                    |
| `customScalars`         | Listcustomscalar |          | List of custom GraphQL scalar to converter mappings containing information about corresponding Java type and converter that should be used to serialize/deserialize values.                                                                                           |
| ``       | File               |          | Target directory where to store generated files.**Default value is**: ``                                                                                                                              |
| ``           | String             | yes      | Target package name for generated code.**User property is**: ``.                                                                                                                                                                       |
| ``    | File               |          | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `` property instead.**Default value is:** ``.                                            |
| ``            | List         |          | List of query files to be processed. Instead of a list of files to be processed you can also specify `` directory containing all the files. If this property is specified it will take precedence over the corresponding directory property. |
| ``            | String             |          | GraphQL schema file that will be used to generate client code.**Default value is**: ``**User property is**: ``.                                                                    |

**Parameter Details**

-   _customScalars_ - List of custom GraphQL scalar to converter mappings containing information about corresponding Java type and converter that should be used to serialize/deserialize values.

    ```xml

    
            UUID
            
            java.util.UUID
            
            com.example.UUIDScalarConverter
        
    --&gt;

    ```

### generate-sdl

Generates GraphQL schema in SDL format from your source code using reflections. Utilizes `graphql-kotlin-schema-generator`
to generate the schema from classes implementing `graphql-kotlin-types` marker `Query`, `Mutation` and `Subscription` interfaces.
In order to limit the amount of packages to scan, this mojo requires users to provide a list of `packages` that can contain
GraphQL types.

This MOJO utilizes [ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html)
mechanism to dynamically load available `SchemaGeneratorHooksProvider` service providers from the classpath. Service provider
can be provided as part of your project, included in one of your project dependencies or through explicitly provided artifact.
See [Schema Generator Hooks Provider](./hooks-provider.md) for additional details on how to create custom hooks service
provider. Configuration below shows how to configure GraphQL Kotlin plugin with explicitly provided artifact.

**Attributes**

-   _Default Lifecycle Phase_: `process-classes`
-   _Requires Maven Project_

**Parameters**

| Property            | Type         | Required | Description                                                                                                       |
| ------------------- | ------------ | -------- | ----------------------------------------------------------------------------------------------------------------- |
| `packages`   | Liststring | yes      | List of supported packages that can be scanned to generate SDL.                                                   |
| `` | File         |          | Target GraphQL schema file to be generated.**Default value is:** `` |

### generate-test-client

Generate GraphQL test client code based on the provided GraphQL schema and target queries.

**Attributes**

-   _Default Lifecycle Phase_: ``
-   _Requires Maven Project_
-   Generated classes are automatically added to the list of test compiled sources.

**Parameters**

| Property                       | Type               | Required | Description                                                                                                                                                                                                                                                           |
| ------------------------------ | ------------------ | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `` | Boolean            |          | Boolean flag indicating whether selection of deprecated fields is allowed or not.**Default value is:** ``.**User property is**: ``.                                                                         |
| ``            | GraphQLClientType  |          | Enum value that specifies target GraphQL client type implementation.**Default value is:** ``.                                                                                                                                    |
| ``         | List |          | List of custom GraphQL scalar to converter mappings containing information about corresponding Java type and converter that should be used to serialize/deserialize values.                                                                                           |
| ``       | File               |          | Target directory where to store generated files.**Default value is**: ``                                                                                                                         |
| ``           | String             | yes      | Target package name for generated code.**User property is**: ``.                                                                                                                                                                       |
| ``    | File               |          | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `` property instead.**Default value is:** ``.                                            |
| ``            | List         |          | List of query files to be processed. Instead of a list of files to be processed you can also specify `` directory containing all the files. If this property is specified it will take precedence over the corresponding directory property. |
| ``            | String             |          | GraphQL schema file that will be used to generate client code.**Default value is**: ``**User property is**: ``.                                                                    |

**Parameter Details**

-   _customScalars_ - List of custom GraphQL scalar to converter mappings containing information about corresponding Java type and converter that should be used to serialize/deserialize values.

    ```xml

    
            UUID
            
            java.util.UUID
            
            com.example.UUIDScalarConverter
        
    --&gt;

    ```

### introspect-schema

Executes GraphQL introspection query against specified `` and saves the result locally to a target file
(defaults to `` under build directory). In general, this goal provides limited functionality by itself and
instead should be used to generate input for the subsequent `` goal.

**Attributes**

-   _Default Lifecycle Phase_: ``

**Parameters**

| Property                      | Type                 | Required | Description                                                                                                                                                                                               |
| ----------------------------- | -------------------- | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ``             | String               | yes      | Target GraphQL server endpoint that will be used to execute introspection queries.**User property is**: ``.                                                                   |
| ``              | Map&lt;String, Any&gt;  |          | Optional HTTP headers to be specified on an introspection query.                                                                                                                                          |
| `` | TimeoutConfiguration |          | Optional timeout configuration (in milliseconds) to download schema from SDL endpoint before we cancel the request.**Default values are:**connect timeout = 5000read timeout = 15000. |
| ``           | File                 |          | Target schema file.**Default value is**: ``**User property is**: ``.                                                   |

**Parameter Details**

-   _timeoutConfiguration_ - Timeout configuration that allows you to specify connect and read timeout values in milliseconds.

    ```xml

    
        1000
        30000
    --&gt;

    ```
