---
id: maven-plugin-usage
title: Maven Plugin Usage
sidebar_label: Usage
---
## Downloading Schema SDL

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

## Introspecting Schema

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

## Generating Generic Client

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

&gt; NOTE: You might need to explicitly add generated clients to your project sources for your IDE to recognize them. See
&gt; [build-helper-maven-plugin](https://www.mojohaus.org/build-helper-maven-plugin/) for details.

## Generating Ktor or WebClient Based Client

By default, GraphQL Kotlin plugins will generate client code that uses generic `GraphQLClient` interface. Additional
configuration options are available if you generate type specific client code but it will also put a restriction on
type of client that can be used for your queries.

For example in order to generate Ktor based HTTP client we need to specify `GraphQLClientType.KTOR` client type. Alternatively,
if you would like to use WebClient implementation instead you need to specify `GraphQLClientType.WEBCLIENT` instead.

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
                <clientType>KTOR</clientType>
                <packageName>com.example.generated</packageName>
                <schemaFile>mySchema.graphql</schemaFile>
            </configuration>
        </execution>
    </executions>
</plugin>

```

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
                <customScalars>
                    <customScalar>
                        <!-- custom scalar UUID type
                        scalarUUID
                         fully qualified Java class name of a custom scalar type
                        typejava.util.UUID
                         fully qualified Java class name of a custom com.expediagroup.graphql.client.converter.ScalarConverter
                           used to convert to/from raw JSON and scalar type
                        convertercom.example.UUIDScalarConverter


                packagenamecom.example.generated
                schemafilemySchema.graphql



--&gt;

```

## Generating Test Client

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

&gt; NOTE: You might need to explicitly add generated test clients to your project test sources for your IDE to recognize them.
&gt; See [build-helper-maven-plugin](https://www.mojohaus.org/build-helper-maven-plugin/) for details.

## Minimal Configuration Example

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
            </configuration>
        </execution>
    </executions>
</plugin>

```

&gt; NOTE: Both `introspect-schema` and `generate-client` goals are bound to the same `generate-sources` Maven lifecycle phase.
&gt; As opposed to Gradle, Maven does not support explicit ordering of different goals bound to the same build phase. Maven
&gt; Mojos will be executed in the order they are defined in your `pom.xml` build file.

## Complete Configuration Example

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
                <!-- optional configuration below
                schemafile${"{"}project.build.directory{"}"}/mySchema.graphql
                allowdeprecatedfieldstrue
                customscalars


                        UUID

                        java.util.UUID

                        com.example.UUIDScalarConverter


                headers
                    My-Custom-Header

                timeoutconfiguration

                    1000
                    30000

                queryfiles
                    ${"{"}project.basedir{"}"}/src/main/resources/queries/MyQuery.graphql




--&gt;

```

## Generating Multiple Clients

In order to generate GraphQL clients targeting multiple endpoints, we need to configure separate executions targeting
different endpoints.

```xml

<plugin>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-maven-plugin</artifactId>
    <version>${graphql-kotlin.version}</version>
    <executions>
        <execution>
            <id>generate-first-client</id>
            <goals>
                <goal>introspect-schema</goal>
                <goal>generate-client</goal>
            </goals>
            <configuration>
                <endpoint>http://localhost:8080/graphql</endpoint>
                <packageName>com.example.generated.first</packageName>
                <queryFiles>
                    <queryFile>${project.basedir}/src/main/resources/queries/FirstQuery.graphql</queryFile>
                </queryFiles>
            </configuration>
        </execution>
        <execution>
            <id>generate-second-client</id>
            <goals>
                <goal>introspect-schema</goal>
                <goal>generate-client</goal>
            </goals>
            <configuration>
                <endpoint>http://localhost:8081/graphql</endpoint>
                <packageName>com.example.generated.second</packageName>
                <queryFiles>
                    <queryFile>${project.basedir}/src/main/resources/queries/SecondQuery.graphql</queryFile>
                </queryFiles>
            </configuration>
        </execution>
    </executions>
</plugin>

```

## Generating SDL Example

GraphQL schema can be generated directly from your source code using reflections. `generate-sdl` mojo will scan your
classpath looking for classes implementing `Query`, `Mutation` and `Subscription` marker interfaces and then generates the
corresponding GraphQL schema using `graphql-kotlin-schema-generator` and default `NoopSchemaGeneratorHooks`. In order to
limit the amount of packages to scan, this mojo requires users to provide a list of `packages` that can contain GraphQL
types.

```xml

<plugin>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-maven-plugin</artifactId>
    <version>${graphql-kotlin.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>generate-sdl</goal>
            </goals>
            <configuration>
                <packages>
                    <package>com.example</package>
                </packages>
            </configuration>
        </execution>
    </executions>
</plugin>

```

## Generating SDL with Custom Hooks Provider Example

Plugin will default to use `NoopSchemaGeneratorHooks` to generate target GraphQL schema. If your project uses custom hooks
or needs to generate the federated GraphQL schema, you will need to provide an instance of `SchemaGeneratorHooksProvider`
service provider that will be used to create an instance of your custom hooks.

`generate-sdl` mojo utilizes [ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html)
mechanism to dynamically load available `SchemaGeneratorHooksProvider` service providers from the classpath. Service provider
can be provided as part of your project, included in one of your project dependencies or through explicitly provided artifact.
See [Schema Generator Hooks Provider](./hooks-provider.mdx) for additional details on how to create custom hooks service provider.
Configuration below shows how to configure GraphQL Kotlin plugin with explicitly provided artifact to generate federated
GraphQL schema.

```xml

<plugin>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-maven-plugin</artifactId>
    <version>${graphql-kotlin.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>generate-sdl</goal>
            </goals>
            <configuration>
                <packages>
                    <package>com.example</package>
                </packages>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>com.expediagroup</groupId>
            <artifactId>graphql-kotlin-federated-hooks-provider</artifactId>
            <version>${graphql-kotlin.version}</version>
        </dependency>
    </dependencies>
</plugin>

```
