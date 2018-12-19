# GraphQL Kotlin

[![Build Status](https://travis-ci.org/ExpediaDotCom/graphql-kotlin.svg?branch=master)](https://travis-ci.org/ExpediaDotCom/graphql-kotlin)
[![codecov](https://codecov.io/gh/ExpediaDotCom/graphql-kotlin/branch/master/graph/badge.svg)](https://codecov.io/gh/ExpediaDotCom/graphql-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/com.expedia/graphql-kotlin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expedia%22%20AND%20a:%22graphql-kotlin%22)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

Most GraphQL libraries for the JVM require developers to maintain two sources of truth for their GraphQL API, the schema and the corresponding code (data fetchers and types). Given the similarities between Kotlin and GraphQL, such as the ability to define nullable/non-nullable types, a schema should be able to be generated from Kotlin code without any separate schema specification. `graphql-kotlin` builds upon `graphql-java` to allow code-only GraphQL services to be built.

For information on GraphQL, please visit [the GraphQL website](https://graphql.org/).

For information on `graphql-java`, please visit [GraphQL Java](https://www.graphql-java.com/documentation/latest/).

## Installation

Using a JVM dependency manager, simply link `graphql-kotlin` to your project.

With Maven:

```xml
<dependency>
  <groupId>com.expedia</groupId>
  <artifactId>graphql-kotlin</artifactId>
  <version>${latestVersion}</version>
</dependency>
```

With Gradle:

```groovy
compile(group: 'com.expedia', name: 'graphql-kotlin', version: "$latestVersion")
```

## Usage


```kotlin
// Your existing Kotlin code

data class Widget(val id: Int, val value: String)

class WidgetQuery {
  fun widgetById(id: Int): Widget? {
    // grabs widget from a data source, might return null
  }
  
  @Deprecated("Use widgetById")
  fun widgetByValue(value: String): Widget? {
    // grabs widget from a deprecated data source, might return null
  }
}

class WidgetMutation {
  fun saveWidget(value: String): Widget {
    // Create and save new widget, returns non-null
  }
}

// Generate the schema

val config = SchemaGeneratorConfig(listOf("org.example"))
val queries = listOf(TopLevelObjectDef(WidgetQuery()))
val mutations = listOf(TopLevelObjectDef(WidgetMutation()))

toSchema(queries, mutations, config)
```

will generate

```graphql
schema {
  query: TopLevelQuery
  mutation: TopLevelMutation
}

type TopLevelQuery {
  widgetById(id: Int!): Widget
  
  widgetByValue(vale: String!): Widget @deprecated(reason: "Use widgetById")
}

type TopLevelMutation {
  saveWidget(value: String!): Widget!
}

type Widget {
  id: Int!
  value: String!
}
```

## Documentation

There are more examples and documention in our [Wiki](https://github.com/ExpediaDotCom/graphql-kotlin/wiki).
