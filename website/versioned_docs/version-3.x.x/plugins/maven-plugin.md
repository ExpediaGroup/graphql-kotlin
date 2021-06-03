---
id: maven-plugin
title: Maven Plugin
original_id: maven-plugin
---
GraphQL Kotlin Maven Plugin provides functionality to introspect GraphQL schemas and generate a lightweight GraphQL HTTP client.

## Goals

You can find detailed information about `graphql-kotlin-maven-plugin` and all its goals by running `mvn help:describe -Dplugin=com.expediagroup:graphql-kotlin-maven-plugin -Ddetail`.

### download-sdl

GraphQL endpoints are often public and as such many servers might disable introspection queries in production environment.
Since GraphQL schema is needed to generate type safe clients, as alternative GraphQL servers might expose private
endpoints (e.g. accessible only from within network, etc) that could be used to download schema in Schema Definition
Language (SDL) directly. This Mojo attempts to download schema from the specified `graphql.endpoint`, validates the
result whether it is a valid schema and saves it locally as `schema.graphql` under build directory. In general, this
goal provides limited functionality by itself and instead should be used to generate input for the subsequent
`generate-client` goal.

**Attributes**

-   _Default Lifecycle Phase_: `generate-sources`

**Parameters**

| Property                      | Type                 | Required | Description                                                                                                                                                                                        |
| ----------------------------- | -------------------- | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `endpoint`             | String               | yes      | Target GraphQL server SDL endpoint that will be used to download schema.<br />**User property is**: `graphql.endpoint`.                                                                      |
| `headers`              | Map&lt;String, Any&gt;  |          | Optional HTTP headers to be specified on a SDL request.                                                                                                                                            |
| `timeoutConfiguration` | TimeoutConfiguration |          | Optional timeout configuration (in milliseconds) to download schema from SDL endpoint before we cancel the request.<br />**Default values are:** connect timeout = 5000, read timeout = 15000.<br /> |

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

| Property                       | Type                            | Required | Description                                                                                                                                                                                                                                                           |
| ------------------------------ | ------------------------------- | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `allowDeprecatedFields` | Boolean                         |          | Boolean flag indicating whether selection of deprecated fields is allowed or not.<br />**Default value is:** `false`.<br />**User property is**: `graphql.allowDeprecatedFields`.                                                                         |
| `converters`            | Map&lt;String, ScalarConverter&gt; |          | Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.                                                                                                    |
| `outputDirectory`       | File                            |          | Target directory where to store generated files.<br />**Default value is**: `${project.build.directory}/generated-sources/graphql`                                                                                                                              |
| `packageName`           | String                          | yes      | Target package name for generated code.<br />**User property is**: `graphql.packageName`.                                                                                                                                                                       |
| `queryFileDirectory`    | File                            |          | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br />**Default value is:** `src/main/resources`.                                            |
| `queryFiles`            | Listfile                      |          | List of query files to be processed. Instead of a list of files to be processed you can also specify `` directory containing all the files. If this property is specified it will take precedence over the corresponding directory property. |
| ``            | String                          | yes      | GraphQL schema file that will be used to generate client code.**User property is**: ``.                                                                                                                                                 |

**Parameter Details**

-   _converters_ - Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.

    ```xml

    
      
        
        java.util.UUID
        
        com.example.UUIDScalarConverter
      
    --&gt;

    ```

### generate-test-client

Generate GraphQL test client code based on the provided GraphQL schema and target queries.

**Attributes**

-   _Default Lifecycle Phase_: ``
-   _Requires Maven Project_
-   Generated classes are automatically added to the list of test compiled sources.

**Parameters**

| Property                       | Type                            | Required | Description                                                                                                                                                                                                                                                           |
| ------------------------------ | ------------------------------- | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `` | Boolean                         |          | Boolean flag indicating whether selection of deprecated fields is allowed or not.**Default value is:** ``.**User property is**: ``.                                                                         |
| ``            | Map&lt;String, ScalarConverter&gt; |          | Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.                                                                                                    |
| ``       | File                            |          | Target directory where to store generated files.**Default value is**: ``                                                                                                                         |
| ``           | String                          | yes      | Target package name for generated code.**User property is**: ``.                                                                                                                                                                       |
| ``    | File                            |          | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `` property instead.**Default value is:** ``.                                            |
| ``            | List                      |          | List of query files to be processed. Instead of a list of files to be processed you can also specify `` directory containing all the files. If this property is specified it will take precedence over the corresponding directory property. |
| ``            | String                          | yes      | GraphQL schema file that will be used to generate client code.**User property is**: ``.                                                                                                                                                 |

**Parameter Details**

-   _converters_ - Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.

    ```xml

    
      
        
        java.util.UUID
        
        com.example.UUIDScalarConverter
      
    --&gt;

    ```

### introspect-schema

Executes GraphQL introspection query against specified `` and saves the underlying schema file as
`` under build directory. In general, this goal provides limited functionality by itself and instead
should be used to generate input for the subsequent `` goal.

**Attributes**

-   _Default Lifecycle Phase_: ``

**Parameters**

| Property                      | Type                 | Required | Description                                                                                                                                                                                 |
| ----------------------------- | -------------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ``             | String               | yes      | Target GraphQL server endpoint that will be used to execute introspection queries.**User property is**: ``.                                                     |
| ``              | Map&lt;String, Any&gt;  |          | Optional HTTP headers to be specified on an introspection query.                                                                                                                            |
| `` | TimeoutConfiguration |          | Optional timeout configuration(in milliseconds) to execute introspection query before we cancel the request.**Default values are:** connect timeout = 5000, read timeout = 15000. |

**Parameter Details**

-   _timeoutConfiguration_ - Timeout configuration that allows you to specify connect and read timeout values in milliseconds.

    ```xml

    
        1000
        30000
    --&gt;

    ```

## Examples

### Downloading Schema SDL

Download SDL Mojo requires target GraphQL server `` to be specified. Task can be executed directly from the
command line by explicitly specifying `` property.

```shell script



```

Mojo can also be configured in your Maven build file

```xml



```

By default, `` goal will be executed as part of the `` [build lifecycle phase](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).

### Introspecting Schema

Introspection Mojo requires target GraphQL server `` to be specified. Task can be executed directly from the
command line by explicitly specifying `` property

```shell script



```

Mojo can also be configured in your Maven build file

```xml



```

By default, `` goal will be executed as part of the `` [build lifecycle phase](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).

### Generating Client

This Mojo generates GraphQL client code based on the provided queries using target GraphQL ``. Classes are
generated under specified ``. When using default configuration and storing GraphQL queries under ``
directories, task can be executed directly from the command line by explicitly providing required properties.

```shell script



```

Mojo can also be configured in your Maven build file to become part of your build lifecycle. Plugin also provides additional
configuration options that are not available on command line.

```xml



```

This will process all GraphQL queries located under `` and generate corresponding GraphQL Kotlin clients.
Generated classes will be automatically added to the project compile sources.

&gt; NOTE: You might need to explicitly add generated clients to your project sources for your IDE to recognize them. See
&gt; [build-helper-maven-plugin](https://www.mojohaus.org/build-helper-maven-plugin/) for details.

### Generating Client with Custom Scalars

By default, all custom GraphQL scalars will be serialized as Strings. You can override this default behavior by specifying
custom [scalar converter](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-client/src/main/kotlin/com/expediagroup/graphql/client/converter/ScalarConverter.kt).

For example given following custom scalar in our GraphQL schema

```graphql



```

We can create a custom converter to automatically convert this custom scalar to ``

```kotlin



```

Afterwards we need to configure our plugin to use this custom converter

```xml


                    
                        
                        java.util.UUID
                        
                        com.example.UUIDScalarConverter
                    
                
                com.example.generated
                mySchema.graphql
            
        
    
--&gt;

```

### Generating Test Client

This Mojo generates GraphQL Kotlin test client code based on the provided queries using target GraphQL ``. Classes
are generated under specified ``. When using default configuration and storing GraphQL queries under ``
directories, task can be executed directly from the command line by explicitly providing required properties.

```shell script



```

Mojo can also be configured in your Maven build file to become part of your build lifecycle. Plugin also provides additional
configuration options that are not available on command line.

```xml



```

This will process all GraphQL queries located under `` and generate corresponding GraphQL Kotlin test clients.
Generated classes will be automatically added to the project test compile sources.

&gt; NOTE: You might need to explicitly add generated test clients to your project test sources for your IDE to recognize them.
&gt; See [build-helper-maven-plugin](https://www.mojohaus.org/build-helper-maven-plugin/) for details.

### Complete Minimal Configuration Example

Following is the minimal configuration that runs introspection query against a target GraphQL server and generates a corresponding schema.
This generated schema is subsequently used to generate GraphQL client code based on the queries provided under `` directory.

```xml



```

&gt; NOTE: Both `` and `` goals are bound to the same `` Maven lifecycle phase.
&gt; As opposed to Gradle, Maven does not support explicit ordering of different goals bound to the same build phase. Maven
&gt; Mojos will be executed in the order they are defined in your `` build file.

### Complete Configuration Example

Following is a configuration example that downloads schema SDL from a target GraphQL server that is then used to generate
the GraphQL client code based on the provided query.

```xml


                true
                
                    
                    
                        
                        java.util.UUID
                        
                        com.example.UUIDScalarConverter
                    
                
                
                    My-Custom-Header
                
                
                    
                    1000
                    30000
                
                
                    ${"{"}project.basedir{"}"}/src/main/resources/queries/MyQuery.graphql
                
            
        
    
--&gt;

```
