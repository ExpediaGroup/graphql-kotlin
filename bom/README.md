# GraphQL Kotlin BOM
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-bom.svg?label=Maven%20Central)](https://central.sonatype.com/search?namespace=com.expediagroup&q=name%3Agraphql-kotlin-bom)

`graphql-kotlin-bom` defines the versions of all the modules in `graphql-kotlin`.
This allows the version of the modules imported in your project to be consistent
without needing to specify the version of each module.

## Usage

With Maven, import a dependency on `graphql-kotlin-bom` in `dependencyManagement` section in your project pom,
and add dependencies on `graphql-kotlin` modules with no version.

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.expediagroup</groupId>
      <artifactId>graphql-kotlin-bom</artifactId>
      <version>${latestVersion}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>com.expediagroup</groupId>
    <artifactId>graphql-kotlin-client</artifactId>
  </dependency>
</dependencies>
```

With Gradle, import `graphql-kotlin-bom` using Gradle `platform`,
and add dependencies on `graphql-kotlin` modules with no version.

```kotlin
implementation(platform("com.expediagroup:graphql-kotlin-bom:$latestVersion"))

implementation("com.expediagroup:graphql-kotlin-client")
```

## Documentation

Additional information can be found in our [documentation](https://opensource.expediagroup.com/graphql-kotlin/docs/server/graphql-server)
and the [Javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-server) of all published library versions.

If you have a question about something you can not find in our documentation or javadocs, feel free to [start a new discussion](https://github.com/ExpediaGroup/graphql-kotlin/discussions).
