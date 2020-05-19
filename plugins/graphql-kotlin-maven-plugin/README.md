# GraphQL Kotlin Maven Plugin

[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-maven-plugin%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-maven-plugin.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-maven-plugin)

GraphQL Kotlin Maven Plugin provides functionality to introspect GraphQL schemas and generate a lightweight GraphQL HTTP client.

## Usage

Plugin should be configured as part of your `pom.xml` build file.

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
        <execution>
            <goals>
                <goal>generate-client</goal>
            </goals>
            <configuration>
                <packageName>com.expediagroup.graphql.generated</packageName>
                <schemaFile>${project.build.directory}/schema.graphql</schemaFile>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Goals

### download-sdl

This Mojo attempts to download schema from the specified `graphql.endpoint`, validates the result whether it is a valid
schema and saves it locally as `schema.graphql` under build directory. In general, this goal provides limited functionality
by itself and instead should be used to generate input for the subsequent `generate-client` goal.

**Attributes**

* *Default Lifecycle Phase*: `generate-sources`

**Parameters**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `endpoint` | String | yes | Target GraphQL server SDL endpoint that will be used to download schema.<br/>**User property is**: `graphql.endpoint`. |

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

## Documentation

Additional information can be found in our [documentation](https://expediagroup.github.io/graphql-kotlin/docs/plugins/maven-plugin)
and the [Javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-maven-plugin) of all published versions.

If you have a question about something you can not find in our documentation or Javadocs, feel free to
[create an issue](https://github.com/ExpediaGroup/graphql-kotlin/issues) and tag it with the question label.
