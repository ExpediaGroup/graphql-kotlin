---
id: maven-plugin
title: Maven Plugin
---

GraphQL Kotlin Maven Plugin provides functionality to introspect GraphQL schemas and generate a lightweight GraphQL HTTP client.

## Goals

### downloadSDL

GraphQL endpoints are often public and as such many servers might disable introspection queries in production environment.
Since GraphQL schema is needed to generate type safe clients, as alternative GraphQL servers might expose private
endpoints (e.g. accessible only from within network, etc) that could be used to download schema in Schema Definition
Language (SDL) directly. This Mojo attempts to download schema from the specified `graphql.endpoint`, validates the
result whether it is a valid schema and saves it locally as `schema.graphql` under build directory. In general, this
goal provides limited functionality by itself and instead should be used to generate input for the subsequent
`generateClient` goal.

**Default Lifecycle Phase**: `generate-sources`

**Parameters**

| Property | Type | Description |
| -------- | ---- | ----------- |
| `endpoint` | String | Target GraphQL server SDL endpoint that will be used to download schema. **User property is**: `graphql.endpoint`. |

### introspectSchema

Executes GraphQL introspection query against specified `graphql.endpoint` and saves the underlying schema file as
`schema.graphql` under build directory. In general, this goal provides limited functionality by itself and instead
should be used to generate input for the subsequent `generateClient` goal.

**Default Lifecycle Phase**: `generate-sources`

**Parameters**

| Property | Type | Description |
| -------- | ---- | ----------- |
| `endpoint` | String | Target GraphQL server endpoint that will be used to execute introspection queries. **User property is**: `graphql.endpoint`. |

## Downloading Schema SDL

DownloadSDL Mojo requires target GraphQL server `endpoint` to be specified. Task can be executed directly from the
command line by explicitly specifying `graphql.endpoint` property

```shell script
$ mvn com.expediagroup:graphql-kotlin-maven-plugin:downloadSDL -Dgraphql.endpoint="http://localhost:8080/graphql"
```

Mojo can also be configured in your Maven build file

```xml
<plugin>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-maven-plugin</artifactId>
    <version>${graphql-kotlin.version}</version>
    <executions>
        <execution>
            <id>download-sdl</id>
            <goals>
                <goal>downloadSDL</goal>
            </goals>
            <configuration>
                <endpoint>http://localhost:8080/sdl</endpoint>
            </configuration>
        </execution>
    </executions>
</plugin>
```

By default, `downloadSDL` goal will be executed as part of the `generate-sources` [build lifecycle phase](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).

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

By default, `introspectSchema` goal will be executed as part of the `generate-sources` [build lifecycle phase](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).
