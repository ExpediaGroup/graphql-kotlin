# GraphQL Kotlin

[![Build Status](https://travis-ci.org/ExpediaGroup/graphql-kotlin.svg?branch=master)](https://travis-ci.org/ExpediaGroup/graphql-kotlin)
[![codecov](https://codecov.io/gh/ExpediaGroup/graphql-kotlin/branch/master/graph/badge.svg)](https://codecov.io/gh/ExpediaGroup/graphql-kotlin)
[![Docs](https://github.com/ExpediaGroup/graphql-kotlin/workflows/Publish%20Docs/badge.svg)](https://expediagroup.github.io/graphql-kotlin)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

GraphQL Kotlin consists of number of libraries that aim to simplify GraphQL integration for Kotlin applications.

## 📦 Modules

* [graphql-kotlin-schema-generator](/graphql-kotlin-schema-generator) - Code only GraphQL schema generation for Kotlin
* [graphql-kotlin-federation](/graphql-kotlin-federation) - Schema generator extension to build federated GraphQL schemas
* [graphql-kotlin-spring-server](/graphql-kotlin-spring-server) - Spring Boot auto-configuration library to create GraphQL web app
* [examples](/examples) - Example apps that use graphql-kotlin libraries to test and demonstrate usages

## ⌨️ Usage

Below is a basic example of how `graphql-kotlin-schema-generator` converts your Kotlin code into a GraphQL schema. For more details, see our documentation below or in the individual module READMEs

```kotlin
// Your existing Kotlin code

data class Widget(val id: Int, val value: String)

class WidgetService {
  fun widgetById(id: Int): Widget? {
    // grabs widget from a data source, might return null
  }
}

// Generate the schema

val config = SchemaGeneratorConfig(supportedPackages = listOf("org.example"))
val queries = listOf(TopLevelObject(WidgetService()))

toSchema(config, queries)
```

will generate

```graphql
type Query {
  widgetById(id: Int!): Widget
}

type Widget {
  id: Int!
  value: String!
}
```

## 📋 Documentation

Examples and documentation are available on our
[documentation](https://expediagroup.github.io/graphql-kotlin),
or in each module README file.

If you have a question about something you can not find in our documentation, the indivdual modules, or javadocs, feel free to [create an issue](https://github.com/ExpediaGroup/graphql-kotlin/issues) and tag it with the question label.

## 👥 Contact

This project is part of Expedia Group open source but also maintained by a dedicated team

* Expedia Group
  * https://expediagroup.com/
  * oss@expediagroup.com
  
* `@ExpediaGroup/graphql-kotlin-committers`
  * Tag us in an issue on Github

## ✏️ Contributing

To get started, please fork the repo and checkout a new branch. You can then build the library locally with Maven

```shell script
mvn clean install
```


See more info in [CONTRIBUTING.md](CONTRIBUTING.md)

## ⚖️ License
This library is licensed under the [Apache License, Version 2.0](LICENSE)
