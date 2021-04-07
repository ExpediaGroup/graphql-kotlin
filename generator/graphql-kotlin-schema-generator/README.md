# GraphQL Kotlin Schema Generator
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-schema-generator.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-schema-generator%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-schema-generator.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-schema-generator)

Most GraphQL libraries require developers to maintain two sources of truth for their GraphQL API: the schema and the corresponding code (data fetchers or resolvers, and types). Given the similarities between Kotlin and GraphQL, such as the ability to define nullable/non-nullable types, a schema can be generated from Kotlin code without any separate schema specification. `graphql-kotlin` builds upon `graphql-java` to allow code-only, or resolver-first, GraphQL services to be built.

For information on GraphQL, please visit [the GraphQL website](https://graphql.org/).

For information on `graphql-java`, please visit [GraphQL Java](https://www.graphql-java.com/documentation/latest/).

## Installation

Using a JVM dependency manager, link `graphql-kotlin-schema-generator` to your project.

With Maven:

```xml
<dependency>
  <groupId>com.expediagroup</groupId>
  <artifactId>graphql-kotlin-schema-generator</artifactId>
  <version>${latestVersion}</version>
</dependency>
```

With Gradle:

```kotlin
implementation("com.expediagroup", "graphql-kotlin-schema-generator", latestVersion)
```

## Usage


```kotlin
// Your existing Kotlin code

data class Widget(val id: Int, val value: String)

class WidgetService {
  fun widgetById(id: Int): Widget? {
    // grabs widget from a data source, might return null
  }

  @Deprecated("Use widgetById")
  fun widgetByValue(value: String): Widget? {
    // grabs widget from a deprecated data source, might return null
  }
}

class WidgetUpdater {
  fun saveWidget(value: String): Widget {
    // Create and save a new widget, returns non-null
  }
}

// Generate the schema

val config = SchemaGeneratorConfig(supportedPackages = listOf("org.example"))
val queries = listOf(TopLevelObject(WidgetService()))
val mutations = listOf(TopLevelObject(WidgetUpdater()))

toSchema(config, queries, mutations)
```

will generate

```graphql
schema {
  query: Query
  mutation: Mutation
}

type Query {
  widgetById(id: Int!): Widget

  widgetByValue(vale: String!): Widget @deprecated(reason: "Use widgetById")
}

type Mutation {
  saveWidget(value: String!): Widget!
}

type Widget {
  id: Int!
  value: String!
}
```

## Documentation

There are more examples and documentation in our [documentation](https://expediagroup.github.io/graphql-kotlin)
or you can view the [javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-schema-generator) for all published versions.

If you have a question about something you can not find in our documentation or javadocs, feel free to [create an issue](https://github.com/ExpediaGroup/graphql-kotlin/issues) and tag it with the question label.
