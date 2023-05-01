---
id: maven-plugin-usage-sdl
title: Maven Plugin SDL Usage
sidebar_label: Generating SDL
---

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
