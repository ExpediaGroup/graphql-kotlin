# Example GraphQL Kotlin server

This is a basic GraphQL server that uses `graphql-kotlin-spring-server` SpringBoot auto-configuration library. See
[documentation](https://expediagroup.github.io/graphql-kotlin/docs/server/spring-server/spring-overview) for details.

## Building locally

This project uses Gradle and you can build it locally using

```shell script
gradle clean build
```

## Running locally

* Run `Application.kt` directly from your IDE
* Alternatively you can also use the Spring Boot plugin by running `gradle bootRun` from the command line

Once the app has started you can explore the example schema by opening the GraphQL Playground endpoint at http://localhost:8080/playground.
