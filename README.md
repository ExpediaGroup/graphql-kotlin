# GraphQL Kotlin

[![Build Status](https://travis-ci.org/ExpediaDotCom/graphql-kotlin.svg?branch=master)](https://travis-ci.org/ExpediaDotCom/graphql-kotlin)
[![codecov](https://codecov.io/gh/ExpediaDotCom/graphql-kotlin/branch/master/graph/badge.svg)](https://codecov.io/gh/ExpediaDotCom/graphql-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/com.expedia/graphql-kotlin.svg?label=maven%20central)](https://search.maven.org/artifact/com.expedia/graphql-kotlin)
[![Javadocs](https://img.shields.io/maven-central/v/com.expedia/graphql-kotlin.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expedia/graphql-kotlin)
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
val queries = listOf(TopLevelObject(WidgetQuery()))
val mutations = listOf(TopLevelObject(WidgetMutation()))

toSchema(queries, mutations, config)
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

There are more examples and documention in our [Wiki](https://github.com/ExpediaDotCom/graphql-kotlin/wiki) or you can view the [javadocs](https://www.javadoc.io/doc/com.expedia/graphql-kotlin) for all published versions

If you have a question about something you can not find in our wiki or javadocs, feel free to [create an issue](https://github.com/ExpediaDotCom/graphql-kotlin/issues) and tag it with the question label.

## Example

One way to run a GraphQL server is with Spring Boot. A sample Spring Boot app that uses `graphql-kotlin`, [graphql-java-servlet](https://github.com/graphql-java-kickstart/graphql-java-servlet) and [graphiql](https://github.com/graphql/graphiql) is provided in the [example folder](https://github.com/ExpediaDotCom/graphql-kotlin/tree/master/example). All the examples used in this documentation should be available in the sample app.

In order to run it you can run [Application.kt](https://github.com/ExpediaDotCom/graphql-kotlin/blob/master/example/src/main/kotlin/com.expedia.graphql.sample/Application.kt) directly from your IDE. Alternatively you can also use the Spring Boot maven plugin by running `mvn spring-boot:run` from the command line. Once the app has started you can explore the example schema by opening GraphiQL endpoint at [http://localhost:8080/graphiql](http://localhost:8080/graphiql).
