# Example usage GraphQL Kotlin Client using Maven

This project is a simple application that use [GraphQL Kotlin Maven plugin](https://expediagroup.github.io/graphql-kotlin/docs/plugins/maven-plugin) 
to auto-generate GraphQL client and then use it to communicate with the target GraphQL server. See [documentation](https://expediagroup.github.io/graphql-kotlin/) 
for details.

## Building locally

This project uses Maven and you can build it locally using

```shell script
./mvnw clean install
```

## Running locally

* [only works after project is build] Run `Application.kt` directly from your IDE
* Alternatively you can also use the Maven exec plugin by running `./mvnw exec:run` from the command line

Application will then attempt to execute few queries and mutations against a target GraphQL server and print out the results.
