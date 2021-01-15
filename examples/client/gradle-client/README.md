# Example usage GraphQL Kotlin Client using Gradle

This project is a simple application that use [GraphQL Kotlin Gradle plugin](https://expediagroup.github.io/graphql-kotlin/docs/plugins/gradle-plugin)
to auto-generate GraphQL client and then use it to communicate with the target GraphQL server. See [client documentation](https://expediagroup.github.io/graphql-kotlin/docs/client/client-overview)
for details.

## Building locally

This project uses Gradle and you can build it locally using

```shell script
gradle clean build
```

## Running locally

* **[only works after project is build]** Run `Application.kt` directly from your IDE
* Alternatively you can also use the Gradle application plugin by running `gradle run` from the command line

Application will then attempt to execute few queries and mutations against a target GraphQL server and print out the results.
