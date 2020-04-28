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
            <id>introspect-schema</id>
            <goals>
                <goal>introspectSchema</goal>
            </goals>
            <configuration>
                <endpoint>http://localhost:8080/graphql</endpoint>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Goals

### introspectSchema

Executes GraphQL introspection query against specified `graphql.endpoint` and saves the underlying schema file as
`schema.graphql` under build directory. In general, this goal provides limited functionality by itself and instead
should be used to generate input for the subsequent `generateClient` goal.

**Default Lifecycle Phase**: `generate-sources`

**Parameters**

| Property | Type | Description |
| -------- | ---- | ----------- |
| `endpoint` | String | Target GraphQL server endpoint that will be used to execute introspection queries. **User property is**: `graphql.endpoint`. |

## Documentation

Additional information can be found in our [documentation](https://expediagroup.github.io/graphql-kotlin/docs/plugins/maven-plugin)
and the [Javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-maven-plugin) of all published versions.

If you have a question about something you can not find in our documentation or Javadocs, feel free to
[create an issue](https://github.com/ExpediaGroup/graphql-kotlin/issues) and tag it with the question label.
