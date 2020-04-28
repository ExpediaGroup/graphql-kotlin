---
id: maven-plugin
title: Maven Plugin
---

GraphQL Kotlin Maven Plugin provides functionality to introspect GraphQL schemas and generate a lightweight GraphQL HTTP client.

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

## Introspecting Schema

Introspection Mojo requires target GraphQL server `endpoint` to be specified. Task can be executed directly from the
command line by explicitly specifying `graphql.endpoint` property

```shell script
$ mvn com.expediagroup:graphql-kotlin-maven-plugin:introspectSchema -Dgraphql.endpoint="http://localhost:8080/graphql"
```

Mojo can also be configured in your Maven build file

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

By default, `introspectSchema` goal will be executed as part of the `generate-sources` [build lifecycle phase](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)
