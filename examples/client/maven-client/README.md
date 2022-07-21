# Example usage GraphQL Kotlin Client using Maven

This project is a simple application that use [GraphQL Kotlin Maven plugin](https://expediagroup.github.io/graphql-kotlin/docs/plugins/maven-plugin)
to auto-generate GraphQL client data model compatible with `Jackson` and then use Spring Webclient based client to communicate
with the target GraphQL server.

See [client documentation](https://expediagroup.github.io/graphql-kotlin/docs/client/client-overview) for additional details.

## Building locally

This project uses Gradle wrapper to invoke underlying Maven build. You can build it locally using

```shell script
gradle clean build
```

Maven POM file references system variables to always point to latest `-SNAPSHOT` version of GraphQL Kotlin modules. In order
to run Maven directly you will need to provide those system variables/update the POM to point to released versions and then
you can build it as

```shell script
./mvnw clean install
```

## Running locally

* Start server in `server-client-example`, see project README for details
* **[only works after project is build]** Run `Application.kt` directly from your IDE
* Alternatively you can also use the Maven exec plugin by running `./mvnw exec:java` from the command line

Application will then attempt to execute few queries and mutations against a target GraphQL server and print out the results.
