TEST!
# GraphQL Kotlin

[![Build Status](https://travis-ci.org/ExpediaDotCom/graphql-kotlin.svg?branch=master)](https://travis-ci.org/ExpediaDotCom/graphql-kotlin) [![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.expedia.www/graphql-kotlin/badge.png)](https://maven-badges.herokuapp.com/maven-central/com.expedia.www/graphql-kotlin)

Most GraphQL libraries for the JVM require developers to maintain two sources of truth for their GraphQL API, the schema and the corresponding code (data fetchers and types). Given the similarities between Kotlin and GraphQL, such as the ability to define nullable/non-nullable types, a schema should be able to be generated from Kotlin code without any separate schema specification. `graphql-kotlin` builds upon `graphql-java` to allow code-only GraphQL services to be built.

For information on GraphQL, please visit [the GraphQL website](https://graphql.org/).

For information on `graphql-java`, please visit [GraphQL Java](https://graphql-java.readthedocs.io/en/latest/).

# Getting started

## Installation

Using a JVM dependency manager, simply link `graphql-kotlin` to your project.

With Maven:

```xml
<dependency>
  <groupId>com.expedia.www</groupId>
  <artifactId>graphql-kotlin</artifactId>
  <version>${latestVersion}</version>
</dependency>
```

With Gradle:

```groovy
compile(group: 'com.expedia.www', artifact: 'graphql-kotlin', version: '${latestVersion}')
```

## Usage

Please refer to our [Wiki](https://github.com/ExpediaDotCom/graphql-kotlin/wiki) for detailed usage information.
