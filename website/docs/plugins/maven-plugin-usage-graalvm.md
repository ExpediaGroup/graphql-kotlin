---
id: maven-plugin-usage-graalvm
title: Maven Plugin GraalVM Usage
sidebar_label: GraalVM Native Image
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

[GraalVm](https://www.graalvm.org/) is a high performance runtime from Oracle that supports Ahead-of-Time (AOT) compilation
that allows you to build native images. By shifting compilation to the build time, we can create binaries that are
**already optimized so they start almost instantaneously with immediate peak performance**. Compiled code is also much
more memory efficient as we no longer need the big memory overhead of running the JVM.

## Generating GraalVM Native Image

In order to generate GraalVM Native image we need to provide the information about all the dynamic JVM features that our
application relies on. Since `graphql-kotlin` generates schema directly from your source code using reflections, we need
to capture this information to make it available at build time. By default, `graphql-kotlin` also relies on classpath scanning
to look up all polymorphic types implementations as well as to locate all the (Apollo) Federated entity types.

Given following schema

```kotlin
class NativeExampleQuery : Query {
    fun helloWorld() = "Hello World"
}
```

We first need to then configure our server to avoid class scanning. Even though our example schema does not contain any
polymorphic types, **we still need to explicitly opt-out of class scanning by providing type hierarchy**.

```kotlin
fun Application.graphQLModule() {
    install(GraphQL) {
        schema {
            packages = listOf("com.example")
            queries = listOf(
                HelloWorldQuery()
            )
        }
        // mapping between interfaces/union KClass and their implementation KClasses
        typeHierarchy = mapOf()
    }
    install(Routing) {
        graphQLPostRoute()
        graphiQLRoute()
    }
}
```

We then need to update our build with native configuration

<Tabs
defaultValue="native"
values={[
{ label: 'Original POM File', value: 'original' },
{ label: 'Native POM File', value: 'native' }
]
}>

<TabItem value="original">

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>example-graalvm-server</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <kotlin.jvmTarget>17</kotlin.jvmTarget>
        <graphql-kotlin.version>${latestGraphqlKotlinVersion}</graphql-kotlin.version>
        <!-- lib versions -->
        <ktor.version>2.2.4</ktor.version>
        <logback.version>1.4.7</logback.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.expediagroup</groupId>
            <artifactId>graphql-kotlin-ktor-server</artifactId>
            <version>${graphql-kotlin.version}</version>
        </dependency>
        <dependency>
            <groupId>io.ktor</groupId>
            <artifactId>ktor-server-cio-jvm</artifactId>
            <version>${ktor.version}</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>${logback.version}</version>
        </dependency>
    </dependencies>

    <build>
        <sourceDirectory>${project.basedir}/src/main/kotlin</sourceDirectory>
        <testSourceDirectory>${project.basedir}/src/test/kotlin</testSourceDirectory>
        <plugins>
            <plugin>
                <groupId>org.jetbrains.kotlin</groupId>
                <artifactId>kotlin-maven-plugin</artifactId>
                <version>${kotlin.version}</version>
                <configuration>
                    <jvmTarget>${kotlin.jvmTarget}</jvmTarget>
                </configuration>
                <executions>
                    <execution>
                        <id>compile</id>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <mainClass>com.expediagroup.graalvm.maven.ApplicationKt</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

</TabItem>
<TabItem value="native">

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>example-graalvm-server</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <kotlin.jvmTarget>17</kotlin.jvmTarget>
        <graphql-kotlin.version>${latestGraphqlKotlinVersion}</graphql-kotlin.version>
        <!-- lib versions -->
        <ktor.version>2.2.4</ktor.version>
        <logback.version>1.4.7</logback.version>
        <native-maven-plugin.version>0.9.21</native-maven-plugin.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.expediagroup</groupId>
            <artifactId>graphql-kotlin-ktor-server</artifactId>
            <version>${graphql-kotlin.version}</version>
        </dependency>
        <dependency>
            <groupId>io.ktor</groupId>
            <artifactId>ktor-server-cio-jvm</artifactId>
            <version>${ktor.version}</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>${logback.version}</version>
        </dependency>
    </dependencies>

    <build>
        <sourceDirectory>${project.basedir}/src/main/kotlin</sourceDirectory>
        <testSourceDirectory>${project.basedir}/src/test/kotlin</testSourceDirectory>
        <plugins>
            <plugin>
                <groupId>org.jetbrains.kotlin</groupId>
                <artifactId>kotlin-maven-plugin</artifactId>
                <version>${kotlin.version}</version>
                <configuration>
                    <jvmTarget>${kotlin.jvmTarget}</jvmTarget>
                </configuration>
                <executions>
                    <execution>
                        <id>compile</id>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <mainClass>com.expediagroup.graalvm.maven.ApplicationKt</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
            <!-- 1. configure GraphQL Kotlin GraalVM plugin -->
            <plugin>
                <groupId>com.expediagroup</groupId>
                <artifactId>graphql-kotlin-maven-plugin</artifactId>
                <version>${graphql-kotlin.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>generate-graalvm-metadata</goal>
                        </goals>
                        <configuration>
                            <packages>com.expediagroup.graalvm</packages>
                            <mainClassName>com.expediagroup.graalvm.maven.ApplicationKt</mainClassName>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

    <!-- 2. configure GraalVM Native Maven plugin -->
    <profiles>
        <profile>
            <id>native</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.graalvm.buildtools</groupId>
                        <artifactId>native-maven-plugin</artifactId>
                        <version>${native-maven-plugin.version}</version>
                        <extensions>true</extensions>
                        <executions>
                            <execution>
                                <id>build-native</id>
                                <goals>
                                    <goal>compile-no-fork</goal>
                                </goals>
                                <phase>package</phase>
                            </execution>
                        </executions>
                        <configuration>
                            <verbose>true</verbose>
                            <buildArgs>
                                <arg>--initialize-at-build-time=io.ktor,kotlin,ch.qos.logback,org.slf4j</arg>
                                <arg>-H:+ReportExceptionStackTraces</arg>
                            </buildArgs>
                            <metadataRepository>
                                <enabled>true</enabled>
                            </metadataRepository>
                        </configuration>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
</project>
```

We need to make following changes to be able to generate GraalVM native image:

1. Configure GraphQL Kotlin plugin to generate GraalVM metadata

:::caution
This goal has to run AFTER `compile` but before `package` phase. It defaults to `process-classes` phase.
:::

2. Configure [GraalVM Native Maven plugin](https://graalvm.github.io/native-build-tools/latest/maven-plugin.html)

:::info
GraalVM recommends to create separate profile that simplifies native image creation. Alternatively you can also generate
native image by explicitly executing `native-image` goal.
:::

</TabItem>
</Tabs>

Once the build is configured we can then generate our native image by running `package` command with `native` profile.

```shell
> ./mvnw -Pnative package
```

Native executable image will then be generated under `target` directory.
