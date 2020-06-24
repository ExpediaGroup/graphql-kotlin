---
id: maven-plugin
title: Maven Plugin
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

* *Default Lifecycle Phase*: `generate-sources`

**Parameters**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `endpoint` | String | yes | Target GraphQL server SDL endpoint that will be used to download schema.<br/>**User property is**: `graphql.endpoint`. |
| `headers` | Map<String, Any> | | Optional HTTP headers to be specified on a SDL request.
| `timeoutConfiguration` | TimeoutConfiguration | | Optional timeout configuration (in milliseconds) to download schema from SDL endpoint before we cancel the request.<br/>**Default values are:** connect timeout = 5000, read timeout = 15000.<br/> |

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
| `converters` | Map<String, ScalarConverter> | | Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values. |
| `outputDirectory` | File | | Target directory where to store generated files.<br/>**Default value is**: `${project.build.directory}/generated-sources/graphql` |
| `packageName` | String | yes | Target package name for generated code.<br/>**User property is**: `graphql.packageName`. |
| `queryFileDirectory` | File | | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br/>**Default value is:** `src/main/resources`. |
| `queryFiles` | List<File> | | List of query files to be processed. Instead of a list of files to be processed you can also specify `queryFileDirectory` directory containing all the files. If this property is specified it will take precedence over the corresponding directory property. |
| `schemaFile` | String | yes | GraphQL schema file that will be used to generate client code.<br/>**User property is**: `graphql.schemaFile`. |

**Parameter Details**

  * *converters* - Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.

    ```xml
    <converters>
      <!-- custom scalar type -->
      <UUID>
        <!-- fully qualified Java class name of a custom scalar type -->
        <type>java.util.UUID</type>
        <!-- fully qualified Java class name of a custom com.expediagroup.graphql.client.converter.ScalarConverter
             used to convert to/from raw JSON and scalar type -->
        <converter>com.example.UUIDScalarConverter</converter>
      </UUID>
    </converters>
    ```

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
| `converters` | Map<String, ScalarConverter> | | Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values. |
| `outputDirectory` | File | | Target directory where to store generated files.<br/>**Default value is**: `${project.build.directory}/generated-test-sources/graphql` |
| `packageName` | String | yes | Target package name for generated code.<br/>**User property is**: `graphql.packageName`. |
| `queryFileDirectory` | File | | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br/>**Default value is:** `src/test/resources`. |
| `queryFiles` | List<File> | | List of query files to be processed. Instead of a list of files to be processed you can also specify `queryFileDirectory` directory containing all the files. If this property is specified it will take precedence over the corresponding directory property. |
| `schemaFile` | String | yes | GraphQL schema file that will be used to generate client code.<br/>**User property is**: `graphql.schemaFile`. |

**Parameter Details**

  * *converters* - Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.

    ```xml
    <converters>
      <!-- custom scalar type -->
      <UUID>
        <!-- fully qualified Java class name of a custom scalar type -->
        <type>java.util.UUID</type>
        <!-- fully qualified Java class name of a custom com.expediagroup.graphql.client.converter.ScalarConverter
             used to convert to/from raw JSON and scalar type -->
        <converter>com.example.UUIDScalarConverter</converter>
      </UUID>
    </converters>
    ```

### introspect-schema

Executes GraphQL introspection query against specified `graphql.endpoint` and saves the underlying schema file as
`schema.graphql` under build directory. In general, this goal provides limited functionality by itself and instead
should be used to generate input for the subsequent `generate-client` goal.

**Attributes**

* *Default Lifecycle Phase*: `generate-sources`

**Parameters**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `endpoint` | String | yes | Target GraphQL server endpoint that will be used to execute introspection queries.<br/>**User property is**: `graphql.endpoint`. |
| `headers` | Map<String, Any> | | Optional HTTP headers to be specified on an introspection query. |
| `timeoutConfiguration` | TimeoutConfiguration | | Optional timeout configuration(in milliseconds) to execute introspection query before we cancel the request.<br/>**Default values are:** connect timeout = 5000, read timeout = 15000.<br/> |

**Parameter Details**

  * *timeoutConfiguration* - Timeout configuration that allows you to specify connect and read timeout values in milliseconds.

    ```xml
    <timeoutConfiguration>
        <!-- timeout values in milliseconds -->
        <connect>1000</connect>
        <read>30000</read>
    </timeoutConfiguration>
    ```

## Examples

### Downloading Schema SDL

Download SDL Mojo requires target GraphQL server `endpoint` to be specified. Task can be executed directly from the
command line by explicitly specifying `graphql.endpoint` property.

```shell script
$ mvn com.expediagroup:graphql-kotlin-maven-plugin:download-sdl -Dgraphql.endpoint="http://localhost:8080/sdl"
```

Mojo can also be configured in your Maven build file

```xml
<plugin>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-maven-plugin</artifactId>
    <version>${graphql-kotlin.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>download-sdl</goal>
            </goals>
            <configuration>
                <endpoint>http://localhost:8080/sdl</endpoint>
            </configuration>
        </execution>
    </executions>
</plugin>
```

By default, `download-sdl` goal will be executed as part of the `generate-sources` [build lifecycle phase](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).

### Introspecting Schema

Introspection Mojo requires target GraphQL server `endpoint` to be specified. Task can be executed directly from the
command line by explicitly specifying `graphql.endpoint` property

```shell script
$ mvn com.expediagroup:graphql-kotlin-maven-plugin:introspect-schema -Dgraphql.endpoint="http://localhost:8080/graphql"
```

Mojo can also be configured in your Maven build file

```xml
<plugin>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-maven-plugin</artifactId>
    <version>${graphql-kotlin.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>introspect-schema</goal>
            </goals>
            <configuration>
                <endpoint>http://localhost:8080/graphql</endpoint>
            </configuration>
        </execution>
    </executions>
</plugin>
```

By default, `introspect-schema` goal will be executed as part of the `generate-sources` [build lifecycle phase](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).

### Generating Client

This Mojo generates GraphQL client code based on the provided queries using target GraphQL `schemaFile`. Classes are
generated under specified `packageName`. When using default configuration and storing GraphQL queries under `src/main/resources`
directories, task can be executed directly from the command line by explicitly providing required properties.

```shell script
$ mvn com.expediagroup:graphql-kotlin-maven-plugin:generate-client -Dgraphql.schemaFile="mySchema.graphql" -Dgraphql.packageName="com.example.generated"
```

Mojo can also be configured in your Maven build file to become part of your build lifecycle. Plugin also provides additional
configuration options that are not available on command line.

```xml
<plugin>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-maven-plugin</artifactId>
    <version>${graphql-kotlin.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>generate-client</goal>
            </goals>
            <configuration>
                <packageName>com.example.generated</packageName>
                <schemaFile>mySchema.graphql</schemaFile>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This will process all GraphQL queries located under `src/main/resources` and generate corresponding GraphQL Kotlin clients.
Generated classes will be automatically added to the project compile sources.

>NOTE: You might need to explicitly add generated clients to your project sources for your IDE to recognize them. See
>[build-helper-maven-plugin](https://www.mojohaus.org/build-helper-maven-plugin/) for details.

### Generating Client with Custom Scalars

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

```xml
<plugin>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-maven-plugin</artifactId>
    <version>${graphql-kotlin.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>generate-client</goal>
            </goals>
            <configuration>
                <allowDeprecatedFields>false</allowDeprecatedFields>
                <converters>
                    <!-- custom scalar UUID type -->
                    <UUID>
                        <!-- fully qualified Java class name of a custom scalar type -->
                        <type>java.util.UUID</type>
                        <!-- fully qualified Java class name of a custom com.expediagroup.graphql.client.converter.ScalarConverter
                           used to convert to/from raw JSON and scalar type -->
                        <converter>com.example.UUIDScalarConverter</converter>
                    </UUID>
                </converters>
                <packageName>com.example.generated</packageName>
                <schemaFile>mySchema.graphql</schemaFile>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Generating Test Client

This Mojo generates GraphQL Kotlin test client code based on the provided queries using target GraphQL `schemaFile`. Classes
are generated under specified `packageName`. When using default configuration and storing GraphQL queries under `src/test/resources`
directories, task can be executed directly from the command line by explicitly providing required properties.

```shell script
$ mvn com.expediagroup:graphql-kotlin-maven-plugin:generate-test-client -Dgraphql.schemaFile="mySchema.graphql" -Dgraphql.packageName="com.example.generated"
```

Mojo can also be configured in your Maven build file to become part of your build lifecycle. Plugin also provides additional
configuration options that are not available on command line.

```xml
<plugin>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-maven-plugin</artifactId>
    <version>${graphql-kotlin.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>generate-test-client</goal>
            </goals>
            <configuration>
                <packageName>com.example.generated</packageName>
                <schemaFile>mySchema.graphql</schemaFile>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This will process all GraphQL queries located under `src/test/resources` and generate corresponding GraphQL Kotlin test clients.
Generated classes will be automatically added to the project test compile sources.

>NOTE: You might need to explicitly add generated test clients to your project test sources for your IDE to recognize them.
>See [build-helper-maven-plugin](https://www.mojohaus.org/build-helper-maven-plugin/) for details.

### Complete Minimal Configuration Example

Following is the minimal configuration that runs introspection query against a target GraphQL server and generates a corresponding schema.
This generated schema is subsequently used to generate GraphQL client code based on the queries provided under `src/main/resources` directory.

```xml
<plugin>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-maven-plugin</artifactId>
    <version>${graphql-kotlin.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>introspect-schema</goal>
                <goal>generate-client</goal>
            </goals>
            <configuration>
                <endpoint>http://localhost:8080/graphql</endpoint>
                <packageName>com.example.generated</packageName>
                <schemaFile>${project.build.directory}/schema.graphql</schemaFile>
            </configuration>
        </execution>
    </executions>
</plugin>
```

>NOTE: Both `introspect-schema` and `generate-client` goals are bound to the same `generate-sources` Maven lifecycle phase.
>As opposed to Gradle, Maven does not support explicit ordering of different goals bound to the same build phase. Maven
>Mojos will be executed in the order they are defined in your `pom.xml` build file.

### Complete Configuration Example

Following is a configuration example that downloads schema SDL from a target GraphQL server that is then used to generate
the GraphQL client code based on the provided query.

```xml
<plugin>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-maven-plugin</artifactId>
    <version>${graphql-kotlin.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>download-sdl</goal>
                <goal>generate-client</goal>
            </goals>
            <configuration>
                <endpoint>http://localhost:8080/sdl</endpoint>
                <packageName>com.example.generated</packageName>
                <schemaFile>${project.build.directory}/schema.graphql</schemaFile>
                <!-- optional configuration below -->
                <allowDeprecatedFields>true</allowDeprecatedFields>
                <converters>
                    <!-- custom scalar UUID type -->
                    <UUID>
                        <!-- fully qualified Java class name of a custom scalar type -->
                        <type>java.util.UUID</type>
                        <!-- fully qualified Java class name of a custom com.expediagroup.graphql.client.converter.ScalarConverter
                           used to convert to/from raw JSON and scalar type -->
                        <converter>com.example.UUIDScalarConverter</converter>
                    </UUID>
                </converters>
                <headers>
                    <X-Custom-Header>My-Custom-Header</X-Custom-Header>
                </headers>
                <timeoutConfiguration>
                    <!-- timeout values in milliseconds -->
                    <connect>1000</connect>
                    <read>30000</read>
                </timeoutConfiguration>
                <queryFiles>
                    <queryFile>${project.basedir}/src/main/resources/queries/MyQuery.graphql</queryFile>
                </queryFiles>
            </configuration>
        </execution>
    </executions>
</plugin>
```
